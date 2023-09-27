package grupo1.services;

import grupo1.dtos.DateSearchDTO;
import grupo1.dtos.ProductDTO;
import grupo1.entities.*;
import grupo1.exceptions.DatabaseException;
import grupo1.exceptions.NotFoundException;
import grupo1.repositories.*;
import grupo1.services.impl.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class ProductServiceTest {

    @Mock
    private IFeatureRepository featureRepository;
    @InjectMocks
    private FeatureServiceImpl featureService;
    @Mock
    private ICityRepository cityRepository;
    @InjectMocks
    private CityServiceImpl cityService;
    @Mock
    private ICategoryRepository categoryRepository;
    @InjectMocks
    private CategoryServiceImpl categoryService;
    @Mock
    private IImageRepository imageRepository;
    @InjectMocks
    private ImageServiceImpl imageService;
    @Mock
    private IProductRepository productRepository;
    @InjectMocks
    private ProductServiceImpl productService;
    private Feature existingFeature;
    private City existingCity;
    private Category existingCategory;
    private Image existingImage;
    private Product existingProduct;

    @BeforeEach
    void setUp() {
        existingFeature = new Feature();
        existingFeature.setId(1);
        existingFeature.setNome("Existing Feature");
        existingFeature.setIcone("Existing Icone");
        featureRepository.save(existingFeature);
        Set<Feature> features = new HashSet<>();
        features.add(existingFeature);

        existingCity = new City();
        existingCity.setId(1);
        existingCity.setNome("Existing City");
        existingCity.setPais("Exising Country");
        cityRepository.save(existingCity);

        existingCategory = new Category();
        existingCategory.setId(1);
        existingCategory.setDescription("Existing Description");
        existingCategory.setQualification("Existing Qualification");
        existingCategory.setUrlImg("Existing URL Image");
        categoryRepository.save(existingCategory);

        existingImage = new Image();
        existingImage.setId(1);
        existingImage.setTitulo("Exisiting Title");
        existingImage.setUrl("Exisiting URL");
        imageRepository.save(existingImage);
        Set<Image> images = new HashSet<>();
        images.add(existingImage);

        existingProduct = new Product();
        existingProduct.setId(1);
        existingProduct.setCaracteristicas(features);
        existingProduct.setCidade(existingCity);
        existingProduct.setCategoria(existingCategory);
        existingProduct.setImagens(images);
        existingProduct.setDescricao("Existing Description");
        existingProduct.setNome("Existing Product");
        productRepository.save(existingProduct);

        when(productRepository.findById(1)).thenReturn(Optional.of(existingProduct));
        when(productRepository.findById(2)).thenReturn(Optional.empty());
    }

    @Test
    void testSaveProduct() {
        Optional<Product> product = productRepository.findById(1);
        assertTrue(product.isPresent());
    }

    @Test
    void saveProduct_DuplicateName() {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setNome("TestProduct");

        when(productRepository.findProductByNome("TestProduct")).thenReturn(Optional.of(new Product()));
        assertThrows(DatabaseException.class, () -> productService.save(productDTO));
    }

    @Test
    void testFindAllProducts() {
        Page<Product> productPage = Page.empty();
        Pageable pageable = Pageable.unpaged();

        when(productRepository.findAll(pageable)).thenReturn(productPage);
        Page<ProductDTO> resultPage = productService.findAll(pageable);

        assertTrue(resultPage.isEmpty());
    }

    @Test
    void testFindProductByIdExisting() {
        Optional<ProductDTO> result = productService.findById(1);

        assertTrue(result.isPresent());
        assertEquals(existingProduct.getNome(), result.get().getNome());
    }

    @Test
    void findByName_ProductFound() {
        Page<ProductDTO> productDTOPage = productService.findByName(PageRequest.of(0, 10), existingProduct.getNome());
        assertFalse(productDTOPage.isEmpty());
    }

    @Test
    void testFindProductByIdNonExisting() {
        assertThrows(NotFoundException.class, () -> productService.findById(2));
    }

    @Test
    void findByCategory_ProductsFound() {
        Page<ProductDTO> productDTOPage = productService.findByCategory(PageRequest.of(0, 10), existingProduct.getCategoria().getId());
        assertFalse(productDTOPage.isEmpty());
    }

    @Test
    void findByCategory_NoProductsFound() {
        Page<ProductDTO> productDTOPage = productService.findByCategory(PageRequest.of(0, 10),200);
        assertTrue(productDTOPage.isEmpty());
    }

    @Test
    void findByCity_ProductsFound() {
        Page<ProductDTO> productDTOPage = productService.findByCity(PageRequest.of(0, 10),1);
        assertFalse(productDTOPage.isEmpty());
    }

    @Test
    void findByCity_NoProductsFound() {
        Page<ProductDTO> productDTOPage = productService.findByCity(PageRequest.of(0, 10),200);
        assertTrue(productDTOPage.isEmpty());
    }

    @Test
    void findByDatesAndCity_ProductsFound() {
        DateSearchDTO dateSearchDTO = new DateSearchDTO();
        dateSearchDTO.setDataInicioPesquisa(LocalDate.parse("2023-01-01"));
        dateSearchDTO.setDataFimPesquisa(LocalDate.parse("2023-01-07"));
        Page<ProductDTO> productDTOPage = productService.findByDatesAndCity(PageRequest.of(0, 10),existingProduct.getCidade().getId(),dateSearchDTO);
        assertTrue(productDTOPage.isEmpty());
    }

    @Test
    void updateProduct_Success() {
        ProductDTO updateProduct = new ProductDTO(existingProduct);
        updateProduct.setDescricao("UPDATED DESCRIPTION");
        ProductDTO updatedProduct = productService.update(1,updateProduct);

        Optional<Product> product = productRepository.findProductByNome(updateProduct.getNome());
        assertEquals(product.get().getDescricao(), updatedProduct.getDescricao());
    }

    @Test
    void updateProduct_NotFound() {
        ProductDTO updateProduct = new ProductDTO(existingProduct);
        assertThrows(NotFoundException.class, () -> productService.update(200,updateProduct));
    }

    @Test
    void deleteProduct_Success() {
        productService.delete(1);
        Optional<Product> product = productRepository.findById(1);
        assertTrue(product.isEmpty());
    }

    @Test
    void deleteProduct_NotFound() {
        assertThrows(NotFoundException.class, () -> productService.delete(200));
    }
}

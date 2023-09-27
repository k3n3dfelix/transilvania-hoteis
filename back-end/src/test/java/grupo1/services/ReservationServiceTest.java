package grupo1.services;

import grupo1.dtos.ReservationDTO;
import grupo1.dtos.ReservationDTOResponse;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(SpringExtension.class)
public class ReservationServiceTest {
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
    @Mock
    private IUserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private IReservationRepository reservationRepository;
    @InjectMocks
    private ReservationServiceImpl reservationService;
    private Feature existingFeature;
    private City existingCity;
    private Category existingCategory;
    private Image existingImage;
    private Product existingProduct;
    private User existingUser;
    private Reservation existingReservation;

    @BeforeEach
    public void setUp() {
        existingUser = new User();
        existingUser.setId(1);
        existingUser.setNome("João");
        existingUser.setSobrenome("Menezes");
        existingUser.setEmail("joaomenezes@email.com");
        existingUser.setSenha("brasil123");

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

        existingReservation = new Reservation();
        existingReservation.setId(1);
        existingReservation.setProduct(existingProduct);
        existingReservation.setCustomer(existingUser);
        existingReservation.setHoraInicioReserva(LocalTime.parse("10:00"));
        existingReservation.setDataInicioReserva(LocalDate.parse("2023-01-01"));
        existingReservation.setDataFimReserva(LocalDate.parse("2023-01-07"));

        when(reservationRepository.findById(1)).thenReturn(Optional.of(existingReservation));
        when(reservationRepository.findById(2)).thenReturn(Optional.empty());
    }

    @Test
    public void testSaveReservation() {
        userRepository.save(existingUser);
        productRepository.save(existingProduct);
        reservationRepository.save(existingReservation);
        Optional<ReservationDTOResponse> reservation = reservationService.findById(1);
        assertTrue(reservation.isPresent());
    }

    @Test
    public void testFindAllReservations() {
        List<Reservation> reservationList = new ArrayList<>();
        when(reservationRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(reservationList));
        Page<ReservationDTO> reservations = reservationService.findAll(Pageable.unpaged());
        verify(reservationRepository, times(1)).findAll(any(Pageable.class));
        assertNotNull(reservations);
    }

    @Test
    public void testFindReservationById() {
        Optional<ReservationDTOResponse> reservation = reservationService.findById(1);
        assertTrue(reservation.isPresent());
    }

    @Test
    public void testUpdateReservation() {
//        Re
//        assertNotNull(updatedReservation);
    }

    @Test
    public void testDeleteReservation() {
        int reservationId = 1;
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(new Reservation()));
        doNothing().when(reservationRepository).deleteById(reservationId);
        when(userRepository.findAll()).thenReturn(new ArrayList<>());
        assertDoesNotThrow(() -> reservationService.delete(reservationId));
        verify(reservationRepository, times(1)).deleteById(reservationId);
    }

    @Test
    public void testDeleteReservationNotFoundException() {
        int reservationId = 1;
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> reservationService.delete(reservationId));
        verify(reservationRepository, never()).deleteById(reservationId);
    }

    @Test
    public void testDeleteReservationDataIntegrityViolationException() {
        int reservationId = 1;
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(new Reservation()));
        doThrow(DataIntegrityViolationException.class).when(reservationRepository).deleteById(reservationId);
        assertThrows(DatabaseException.class, () -> reservationService.delete(reservationId));
        verify(reservationRepository, times(1)).deleteById(reservationId);
    }
}


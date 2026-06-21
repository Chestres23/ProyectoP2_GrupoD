package ec.edu.espe.backend.service;

import ec.edu.espe.backend.domain.LostItem;
import ec.edu.espe.backend.domain.User;
import ec.edu.espe.backend.domain.enums.ItemStatus;
import ec.edu.espe.backend.dto.LostItemRequestDTO;
import ec.edu.espe.backend.dto.LostItemResponseDTO;
import ec.edu.espe.backend.exception.InvalidItemStateException;
import ec.edu.espe.backend.exception.ItemNotFoundException;
import ec.edu.espe.backend.repository.LostItemRepository;
import ec.edu.espe.backend.repository.UserRepository;
import ec.edu.espe.backend.service.impl.LostItemServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class LostItemServiceTest {

    @Mock
    private LostItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LostItemServiceImpl service;

    private User createTestUser() {
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@espe.edu.ec");
        user.setPassword("password");
        user.setActive(true);
        user.setRole(User.Role.USER);
        return user;
    }

    private LostItem createTestItem(User user) {
        LostItem item = new LostItem();
        item.setId(1L);
        item.setName("Mochila azul");
        item.setDescription("Mochila encontrada");
        item.setCategory("Bolsos");
        item.setLocationFound("Biblioteca");
        item.setDateFound(LocalDate.of(2026, 6, 20));
        item.setStatus(ItemStatus.FOUND);
        item.setActive(true);
        item.setUser(user);
        return item;
    }

    @Test
    void shouldCreateItem() {
        User user = createTestUser();
        LostItemRequestDTO request = new LostItemRequestDTO();
        request.setName("Celular Samsung");
        request.setDescription("Celular encontrado en aula");
        request.setCategory("Tecnología");
        request.setLocationFound("Aula 101");
        request.setDateFound(LocalDate.of(2026, 6, 20));
        request.setUserId(1L);

        LostItem savedItem = new LostItem();
        savedItem.setId(10L);
        savedItem.setName("Celular Samsung");
        savedItem.setDescription("Celular encontrado en aula");
        savedItem.setCategory("Tecnología");
        savedItem.setLocationFound("Aula 101");
        savedItem.setDateFound(LocalDate.of(2026, 6, 20));
        savedItem.setStatus(ItemStatus.FOUND);
        savedItem.setActive(true);
        savedItem.setUser(user);

        given(userRepository.findById(eq(1L))).willReturn(Optional.of(user));
        given(itemRepository.save(any(LostItem.class))).willReturn(savedItem);

        LostItemResponseDTO result = service.createItem(request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getName()).isEqualTo("Celular Samsung");
        assertThat(result.getCategory()).isEqualTo("Tecnología");
        assertThat(result.getReporterName()).isEqualTo("Test User");
    }

    @Test
    void shouldGetAllActiveItems() {
        User user = createTestUser();
        LostItem item = createTestItem(user);

        given(itemRepository.findByActiveTrue()).willReturn(List.of(item));

        List<LostItemResponseDTO> result = service.getAllActiveItems();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Mochila azul");
    }

    @Test
    void shouldGetItemById() {
        User user = createTestUser();
        LostItem item = createTestItem(user);

        given(itemRepository.findByIdAndActiveTrue(eq(1L))).willReturn(Optional.of(item));

        LostItemResponseDTO result = service.getItemById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Mochila azul");
        assertThat(result.getCategory()).isEqualTo("Bolsos");
    }

    @Test
    void shouldThrowWhenItemNotFoundById() {
        given(itemRepository.findByIdAndActiveTrue(eq(999L))).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.getItemById(999L))
                .isInstanceOf(ItemNotFoundException.class);
    }

    @Test
    void shouldClaimItemInFoundStatus() {
        User user = createTestUser();
        LostItem item = createTestItem(user);

        LostItem claimedItem = createTestItem(user);
        claimedItem.setStatus(ItemStatus.CLAIMED);

        given(itemRepository.findByIdAndActiveTrue(eq(1L))).willReturn(Optional.of(item));
        given(itemRepository.save(any(LostItem.class))).willReturn(claimedItem);

        LostItemResponseDTO result = service.claimItem(1L);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(ItemStatus.CLAIMED);
    }

    @Test
    void shouldNotClaimItemNotInFoundStatus() {
        User user = createTestUser();
        LostItem item = createTestItem(user);
        item.setStatus(ItemStatus.CLAIMED);

        given(itemRepository.findByIdAndActiveTrue(eq(1L))).willReturn(Optional.of(item));

        assertThatThrownBy(() -> service.claimItem(1L))
                .isInstanceOf(InvalidItemStateException.class);
    }

    @Test
    void shouldDeliverItemInClaimedStatus() {
        User user = createTestUser();
        LostItem item = createTestItem(user);
        item.setStatus(ItemStatus.CLAIMED);

        LostItem deliveredItem = createTestItem(user);
        deliveredItem.setStatus(ItemStatus.DELIVERED);
        deliveredItem.setActive(false);

        given(itemRepository.findByIdAndActiveTrue(eq(1L))).willReturn(Optional.of(item));
        given(itemRepository.save(any(LostItem.class))).willReturn(deliveredItem);

        LostItemResponseDTO result = service.deliverItem(1L);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(ItemStatus.DELIVERED);
    }

    @Test
    void shouldNotDeliverItemNotInClaimedStatus() {
        User user = createTestUser();
        LostItem item = createTestItem(user);
        item.setStatus(ItemStatus.FOUND);

        given(itemRepository.findByIdAndActiveTrue(eq(1L))).willReturn(Optional.of(item));

        assertThatThrownBy(() -> service.deliverItem(1L))
                .isInstanceOf(InvalidItemStateException.class);
    }

    @Test
    void shouldSoftDeleteItem() {
        User user = createTestUser();
        LostItem item = createTestItem(user);

        given(itemRepository.findByIdAndActiveTrue(eq(1L))).willReturn(Optional.of(item));
        given(itemRepository.save(any(LostItem.class))).willReturn(item);

        service.deleteItem(1L);

        verify(itemRepository).save(any(LostItem.class));
    }

    @Test
    void shouldUploadImage() throws IOException {
        User user = createTestUser();
        LostItem item = createTestItem(user);

        MultipartFile mockFile = mock(MultipartFile.class);
        given(mockFile.getBytes()).willReturn(new byte[]{1, 2, 3});
        given(mockFile.getContentType()).willReturn("image/png");

        given(itemRepository.findByIdAndActiveTrue(eq(1L))).willReturn(Optional.of(item));
        given(itemRepository.save(any(LostItem.class))).willReturn(item);

        service.uploadImage(1L, mockFile);

        verify(itemRepository).save(any(LostItem.class));
    }

    @Test
    void shouldGetImageBytes() {
        User user = createTestUser();
        LostItem item = createTestItem(user);
        item.setImageData(new byte[]{10, 20, 30});

        given(itemRepository.findById(eq(1L))).willReturn(Optional.of(item));

        byte[] result = service.getImageBytes(1L);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);
    }

    @Test
    void shouldThrowWhenImageBytesNotFound() {
        given(itemRepository.findById(eq(999L))).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.getImageBytes(999L))
                .isInstanceOf(ItemNotFoundException.class);
    }

    @Test
    void shouldGetImageContentType() {
        User user = createTestUser();
        LostItem item = createTestItem(user);
        item.setImageType("image/png");

        given(itemRepository.findById(eq(1L))).willReturn(Optional.of(item));

        String contentType = service.getImageContentType(1L);

        assertThat(contentType).isEqualTo("image/png");
    }

    @Test
    void shouldReturnDefaultContentTypeWhenItemNotFound() {
        given(itemRepository.findById(eq(999L))).willReturn(Optional.empty());

        String contentType = service.getImageContentType(999L);

        assertThat(contentType).isEqualTo("image/jpeg");
    }
}

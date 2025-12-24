//package com.example.payment_service;
//
//import com.example.common.dto.PaymentResponse;
//import com.example.common.dto.PaymentStatus;
//import com.example.payment_service.dto.PaymentResponseDTO;
//import com.example.payment_service.model.PaymentModel;
//import com.example.payment_service.repository.PaymentRepository;
//import com.example.payment_service.service.PaymentService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestInstance;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Import;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.kafka.test.context.EmbeddedKafka;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.reactive.function.client.WebClient;
//import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
//
//import reactor.core.publisher.Mono;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.*;
//
//@SpringBootTest
//@AutoConfigureMockMvc  // для тестирования контроллеров через MockMvc
//@TestInstance(TestInstance.Lifecycle.PER_CLASS) // чтобы setup один раз
//@Transactional // откатывает изменения после каждого теста
//@ActiveProfiles("test")  // <--- вот это ключ
//@EmbeddedKafka(
//		partitions = 1,
//		topics = {"payment-topic", "inventory-topic"} // создаем топики, которые нужны тестируемому сервису
//)
/// /@Import(PaymentServiceTestConfig.class)
//@ExtendWith(MockitoExtension.class)
//class PaymentServiceApplicationTests {
//
//
//	@Autowired
//	private PaymentService paymentService;
//
//	@Autowired
//	private PaymentRepository paymentRepository;
//
//	@Autowired
//	private WebClient webClient; // наш мок
//
//	@Mock
//	KafkaTemplate<String, Object> kafkaTemplate;
//
//	@TestConfiguration
//	static class TestConfig {мс ооь
//		@Bean
//		public WebClient paymentWebClient() {
//			WebClient mockWebClient = mock(WebClient.class);
//			WebClient.RequestHeadersUriSpec<?> uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
//			WebClient.RequestHeadersSpec<?> headersSpec = mock(WebClient.RequestHeadersSpec.class);
//			WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
//
//			when(mockWebClient.get()).thenReturn(uriSpec);
//			when(uriSpec.uri(anyString())).thenReturn(headersSpec);
//			when(headersSpec.retrieve()).thenReturn(responseSpec);
//			when(responseSpec.bodyToMono(PaymentResponse.class))
//					.thenReturn(Mono.just(new PaymentResponse(100, PaymentStatus.SUCCESS)));
//
//			return mockWebClient;
//		}
//
//
//		@Test
//	void testPaymentSuccess() {
//		WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
//		WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
//		WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
//
/// / 1. GET
//		when(webClient.get()).thenReturn(uriSpec);
//
//// 2. uri()
//		when(uriSpec.uri(anyString())).thenReturn(headersSpec);
//
//// 3. retrieve()
//		when(headersSpec.retrieve()).thenReturn(responseSpec);
//
//// 4. bodyToMono
//		when(responseSpec.bodyToMono(PaymentResponseDTO.class))
//				.thenReturn(Mono.just(new PaymentResponseDTO("order123", 100, PaymentStatus.SUCCESS)));
//
//
//
//
//
//		//		PaymentResponse result = paymentService.payForOrder("order123", 150);
////
////		// Assert DB saved correct status
////		PaymentModel model = paymentRepository.findById("order123").orElseThrow();
////		assertEquals(PaymentStatus.SUCCESS, model.getStatus());
////
////		// Assert returned value
////		assertEquals(PaymentStatus.SUCCESS, result.paymentStatus());
////
////		// Assert event was sent
////		verify(kafkaTemplate, times(1))
////				.send(eq("payment-topic"), eq("order123"), any(PaymentConfirmedEvent.class));
//		// Вызов метода
//		PaymentResponse response = paymentService.payForOrder("order123", 100);
//
//		// Проверки
//		assertEquals(PaymentStatus.SUCCESS, response.paymentStatus());
//
//		// Проверка вызова Kafka
//		verify(kafkaTemplate, times(1))
//				.send(eq("payment-topic"), eq("order123"), any());
//	}
//
//
////	@Test
////	void testPaymentFail() {
////		// Мокаем WebClient на неуспешный платеж
////		when(paymentWebClient.get()
////				.uri(any(String.class))
////				.retrieve()
////				.bodyToMono(any(Class.class)))
////				.thenReturn(Mono.just(new com.example.common.dto.PaymentResponse(100, com.example.common.dto.PaymentStatus.FAIL)));
////
////		var response = paymentService.payForOrder("order456", 100);
////
////		PaymentModel paymentModel = paymentRepository.findById("order456").orElseThrow();
////		assertEquals(com.example.common.dto.PaymentStatus.FAIL, paymentModel.getStatus());
////
////		assertEquals(com.example.common.dto.PaymentStatus.FAIL, response.paymentStatus());
////	}
//	@Test
//	void contextLoads() {
//	}
//
//}

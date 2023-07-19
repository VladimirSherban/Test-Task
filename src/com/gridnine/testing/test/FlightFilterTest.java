package com.gridnine.testing.test;

import com.gridnine.testing.Flight;
import com.gridnine.testing.service.FilterService;
import com.gridnine.testing.service.FilterServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.gridnine.testing.FlightBuilder.createFlight;
import static org.junit.jupiter.api.Assertions.*;

public class FlightFilterTest {

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outContent;
    private final FilterService filterService = new FilterServiceImpl();
    private final LocalDateTime threeDaysFromNow = LocalDateTime.now().plusDays(3);
    private final List<Flight> flights = new ArrayList<>();

    @BeforeEach
    public void setUpStreams() {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    public void testFilterDepartureCurrentTimeNoFlightsBeforeCurrentTime() {

        // Добавляем рейсы, которые не прошли текущее время
        flights.add(createFlight(threeDaysFromNow, threeDaysFromNow.plusHours(2)));
        flights.add(createFlight(threeDaysFromNow, threeDaysFromNow.plusHours(2),
                threeDaysFromNow.plusHours(3), threeDaysFromNow.plusHours(5)));

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        filterService.filterDepartureCurrentTime(flights);

        // Проверяем, что ничего не было выведено в консоль (т.е. нет рейсов до текущего времени)
        assertEquals("", outContent.toString());
    }

    @Test
    public void testFilterDepartureCurrentTimeFlightsBeforeCurrentTime() {
        // Создаем тестовые рейсы
        LocalDateTime currentDateTime = LocalDateTime.now();
        List<Flight> flights = new ArrayList<>();
        Flight flight1 = createFlight(currentDateTime.minusHours(2), currentDateTime.minusHours(1));
        Flight flight2 = createFlight(currentDateTime.minusDays(1), currentDateTime.minusHours(1));
        flights.add(flight1);
        flights.add(flight2);

        // Вызываем метод filterDepartureCurrentTime, который должен вывести рейсы в консоль
        filterService.filterDepartureCurrentTime(flights);

        // Получаем результат вывода в консоль
        // Удаляем все символы пробела, включая символы новой строки
        String consoleOutput = outContent.toString().replaceAll("\\s", "");

        // Проверяем, что ожидаемые рейсы были выведены в консоль
        assertEquals(flight1 + flight2.toString(), consoleOutput);
    }

    @Test
    public void testFilterArrivalDateBeforeDepartureDateNoSegmentsBeforeDeparture() {

        flights.add(createFlight(threeDaysFromNow, threeDaysFromNow.plusHours(3)));
        flights.add(createFlight(threeDaysFromNow, threeDaysFromNow.plusHours(2),
                threeDaysFromNow.plusHours(3), threeDaysFromNow.plusHours(5)));

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();

        System.setOut(new PrintStream(outContent));
        filterService.filterArrivalDateBeforeDepartureDate(flights);

        // Проверяем, что ничего не было выведено в консоль (т.е. нет сегментов с датой прилета раньше даты вылета)
        assertEquals("", outContent.toString());
    }

    @Test
    public void testFilterArrivalDateBeforeDepartureDateSegmentsBeforeDeparture() {
        // Создаем тестовые рейсы с сегментами, у которых дата прилета раньше даты вылета
        LocalDateTime now = LocalDateTime.now();
        Flight flight = createFlight(now.plusHours(1), now.minusHours(1)); // Рейс с сегментом, у которого дата прилета раньше даты вылета
        flights.add(flight);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        filterService.filterArrivalDateBeforeDepartureDate(flights);

        // Получаем результат вывода в консоль
        String consoleOutput = outContent.toString().replaceAll("\\s", "");

        // Подготавливаем ожидаемый результат (текст сегментов, у которых дата прилета раньше даты вылета)
        String expectedOutput = flight.getSegments()
                .stream()
                .map(Object::toString)
                .collect(Collectors.joining());

        // Проверяем, что сегменты с датой прилета раньше даты вылета были выведены в консоль
        assertEquals(expectedOutput, consoleOutput);
    }

    @Test
    public void testFilterTimeOnEarthMoreThan() {
        // Создаем тестовые рейсы с разным временем на земле
        LocalDateTime now = LocalDateTime.now();

        // Рейс с временем на земле 30 минут (меньше указанного значения)
        Flight flight1 = createFlight(now.minusHours(2), now, now.plusMinutes(30), now.plusMinutes(45));
        flights.add(flight1);

        // Рейс с временем на земле 120 минут (равное указанному значению)
        Flight flight2 = createFlight(now.minusHours(2), now.minusHours(1), now.plusHours(1), now.plusMinutes(150));
        flights.add(flight2);

        // Рейс с временем на земле 180 минут (больше указанного значения)
        Flight flight3 = createFlight(now.minusHours(2), now.minusHours(1), now.plusHours(2), now.plusMinutes(210));
        flights.add(flight3);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Задаем значение minutes для фильтрации
        int minutes = 120;

        FilterService filterService = new FilterServiceImpl();
        filterService.filterTimeOnEarthMoreThan(flights, minutes);

        // Получаем результат вывода в консоль
        String consoleOutput = outContent.toString();

        // Проверяем, что только рейсы с временем на земле больше указанного значения были выведены в консоль
        assertFalse(consoleOutput.contains(flight1.toString())); // Рейс с временем на земле 30 минут не должен выводиться
        assertFalse(consoleOutput.contains(flight2.toString()));  // Рейс с временем на земле 120 минут не должен выводиться
        assertTrue(consoleOutput.contains(flight3.toString()));  // Рейс с временем на земле 180 минут должен выводиться
    }
}
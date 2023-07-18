package service;

import com.gridnine.testing.Flight;
import com.gridnine.testing.Segment;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class FilterServiceImpl implements FilterService {

    @Override
    public void filterDepartureCurrentTime(List<Flight> flights) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        for (Flight flight : flights) {
            flight.getSegments().stream()
                    .filter(segment -> segment.getDepartureDate().isBefore(currentDateTime))
                    .forEach(System.out::println);
        }
    }

    @Override
    public void filterArrivalDateBeforeDepartureDate(List<Flight> flights) {
        for (Flight flight : flights) {
            flight.getSegments().stream()
                    .filter(segment -> segment.getDepartureDate().isAfter(segment.getArrivalDate()))
                    .forEach(System.out::println);
        }
    }

    @Override
    public void filterTimeOnEarthMoreThan(List<Flight> flights, Integer minutes) {
        System.out.println("Общее время, проведённое на земле превышает " + minutes + " минут");
        for (Flight flight : flights) {
            if (groundTimeMoreThan(flight, Duration.ofMinutes(minutes))) {
                System.out.println(flight);
            }
        }
    }

    /**
     * Данный метод проверяет, превышает ли общее время, проведенное на земле для указанного рейса, заданную длительность groundTime.
     * Объект типа Flight, для которого выполняется проверка общего времени на земле.
     * Объект типа Duration, представляющий заданную длительность времени на земле.
     */
    private static Boolean groundTimeMoreThan(Flight flight, Duration groundTime) {

        if (flight.getSegments().size() <= 1) {
            return false;
        }
        Duration totalGroundTime = Duration.ZERO;
        LocalDateTime previousArrival = null;

        for (Segment segment : flight.getSegments()) {
            if (previousArrival != null) {
                Duration currentGroundTime = Duration.between(previousArrival, segment.getDepartureDate());
                totalGroundTime = totalGroundTime.plus(currentGroundTime);
            }
            previousArrival = segment.getArrivalDate();
        }
        return totalGroundTime.compareTo(groundTime) > 0;
    }
}

import com.gridnine.testing.Flight;
import com.gridnine.testing.FlightBuilder;
import service.FilterService;
import service.FilterServiceImpl;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Flight> flights = FlightBuilder.createFlights();
        System.out.println(flights);
        System.out.println();

        FilterService service = new FilterServiceImpl();

        System.out.println("Вылет до текущего момента времени");
        service.filterDepartureCurrentTime(flights);

        System.out.println("Имеются сегменты с датой прилёта раньше даты вылета");
        service.filterArrivalDateBeforeDepartureDate(flights);
        service.filterTimeOnEarthMoreThan(flights, 120);

    }

}

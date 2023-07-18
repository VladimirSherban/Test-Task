package service;

import com.gridnine.testing.Flight;

import java.util.List;

/**
 * Интерфейс для фильтрации авиаперелетов
 */
public interface FilterService {

    /**
     * Фильтрует и выводит информацию о рейсах, у которых дата вылета раньше текущего момента времени.
     *
     * @param flights Список объектов, содержащий информацию о различных авиарейсах.
     */
    void filterDepartureCurrentTime(List<Flight> flights);


    /**
     * Фильтрует и выводит информацию о рейсах, у которых имеются сегменты с датой прилета раньше даты вылета.
     *
     * @param flights Список объектов, содержащий информацию о различных авиарейсах.
     */
    void filterArrivalDateBeforeDepartureDate(List<Flight> flights);

    /**
     * Фильтрует и выводит информацию о рейсах, у которых общее время,
     * проведенное на земле (время между прилетом одного сегмента и вылетом следующего), превышает указанное количество минут.
     *
     * @param flights Список объектов, содержащий информацию о различных авиарейсах.
     * @param minutes Целое число, представляющее количество минут, по которому выполняется фильтрация.
     */
    void filterTimeOnEarthMoreThan(List<Flight> flights, Integer minutes);

}

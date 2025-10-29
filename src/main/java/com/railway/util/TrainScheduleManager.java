package com.railway.util;

import com.railway.model.Train;

import java.util.*;
import java.util.stream.Collectors;

public class TrainScheduleManager {
    private final Map<String, Train> trainMap = new HashMap<>();
    private final List<Train> trainList = new ArrayList<>();
    private final Set<String> routeSet = new HashSet<>();

    public void addTrain(Train train) {
        trainMap.put(train.getId(), train);
        trainList.add(train);
        routeSet.add(train.getSource() + "->" + train.getDestination());
    }

    public Train findTrainById(String trainId) {
        return trainMap.get(trainId);
    }

    public List<Train> filterTrainsByRoute(String source, String destination) {
        return trainList.stream()
                .filter(t -> t.getSource().equals(source) && t.getDestination().equals(destination))
                .collect(Collectors.toList());
    }

    public List<Train> sortTrainsByFare() {
        return trainList.stream()
                .sorted(Comparator.comparing(Train::getFare))
                .collect(Collectors.toList());
    }

    public Map<String, List<Train>> groupTrainsBySource() {
        return trainList.stream().collect(Collectors.groupingBy(Train::getSource));
    }

    public Set<String> getRouteSet() {
        return Collections.unmodifiableSet(routeSet);
    }
}





package at.ac.tuwien.sepm.groupphase.backend.service.validator;

import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component
public class LocationValidator {

    public List<Location> validateLocationDistance(Double latitude, Double longitude, Double radius, List<Location> locations) {

        List<Location> result = new LinkedList<>();

        for (Location l : locations) {
            if (inRadius(latitude, longitude, l, radius)) {
                result.add(l);
            }
        }

        return result;
    }

    public boolean inRadius(Double lat1, Double lon1, Location l, Double radius) {

        double r = 6371;    // earth radius [km]

        double lat2 = l.getLatitude();
        double lon2 = l.getLongitude();

        double dLat = deg2rad(lat2 - lat1);
        double dLong = deg2rad(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
                Math.sin(dLong / 2) * Math.sin(dLong / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = r * c;        // distance [km]

        return d <= radius;

    }

    public double deg2rad(double deg) {
        return deg * (Math.PI / 180);
    }


}

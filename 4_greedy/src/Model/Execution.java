package Model;

import java.io.Serializable;
import java.util.ArrayList;

import utils.Algorithms;

public record Execution(ArrayList<GeoPoint> geoPoints, Algorithms algorithm) implements Serializable{
}

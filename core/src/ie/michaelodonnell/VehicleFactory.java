package ie.michaelodonnell;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import java.util.ArrayList;
import java.util.Random;

import ie.michaelodonnell.simulation.Street;
import ie.michaelodonnell.simulation.Vehicle;

public class VehicleFactory {
    private int maxVehicles;
    private int maxSpeed;
    private ArrayList<TextureAtlas.AtlasRegion> texturesRegions = new ArrayList<TextureAtlas.AtlasRegion>();
    private TextureAtlas textureAtlas;

    public VehicleFactory() {
        // Initialize the vehicle textures:
        textureAtlas = new TextureAtlas(Gdx.files.internal("cars.txt"));
        texturesRegions.add(textureAtlas.findRegion("car_black_roadster"));
        texturesRegions.add(textureAtlas.findRegion("car_blue"));
        texturesRegions.add(textureAtlas.findRegion("car_blue_small"));
        texturesRegions.add(textureAtlas.findRegion("car_orange"));
        texturesRegions.add(textureAtlas.findRegion("car_red"));
        texturesRegions.add(textureAtlas.findRegion("car_silver_roadster"));
        texturesRegions.add(textureAtlas.findRegion("car_white_roadster"));
        texturesRegions.add(textureAtlas.findRegion("car_white_small"));
        texturesRegions.add(textureAtlas.findRegion("car_yellow_small"));
    }

    public ArrayList<Vehicle> createVehicles(ArrayList<Street> streets, int maxVehicles, int maxSpeed) {
        this.maxVehicles = maxVehicles;
        this.maxSpeed = maxSpeed;
        ArrayList<Vehicle> vehicles = new ArrayList<Vehicle>();
        for (int x = 1; x < this.maxVehicles + 1; x++) {
            int minSpeed = 1;
            int speed = new Random().nextInt((this.maxSpeed - minSpeed) + 1) + minSpeed;
            TextureAtlas.AtlasRegion texture = texturesRegions.get(new Random().nextInt(texturesRegions.size()));
            Street street = streets.get(new Random().nextInt(streets.size()));
            vehicles.add(new Vehicle(x, texture, street.getPosX(), street.getPosY(), speed, this.maxSpeed, street.getDirection()));
        }
        return vehicles;
    }

}

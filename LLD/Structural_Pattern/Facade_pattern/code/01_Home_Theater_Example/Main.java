public class Main {
    public static void main(String[] args) {
        DvdPlayer dvd = new DvdPlayer();
        Projector projector = new Projector();
        Amplifier amp = new Amplifier();
        TheaterLights lights = new TheaterLights();
        Screen screen = new Screen();

        HomeTheaterFacade homeTheater = new HomeTheaterFacade(dvd, projector, amp, lights, screen);

        homeTheater.watchMovie("Interstellar");
        homeTheater.endMovie();
    }
}

public class CarAi extends BaseAi {
    public CarAi(Habitat habitat) {
        this.habitat = habitat;

        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        while(true) {
            synchronized(habitat.monitor) {
                if (Habitat.objectsRunning[0]) {
                    habitat.monitor.notify();
                    for (GameObject object : habitat.objects) {
                        if (object instanceof Car) {object.move();}
                    }
                }
                else {
                    try {
                        habitat.monitor.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
            try {
                Thread.sleep(1000 / frequency);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

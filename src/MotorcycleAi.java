public class MotorcycleAi extends BaseAi {
    public MotorcycleAi(Habitat habitat) {
        this.habitat = habitat;

        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        while(true) {
            synchronized(habitat.monitor) {
                if (Habitat.objectsRunning[1]) {
                    habitat.monitor.notify();
                    for (GameObject object : habitat.objects) {
                        if (object instanceof Motorcycle) {object.move();}
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

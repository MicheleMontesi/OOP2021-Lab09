package it.unibo.oop.lab.reactivegui03;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class AnotherConcurrentGUI extends JFrame{

    /**
     * 
     */
    private final static long serialVersionUID = -8710276539980695794L;
    private final static double WIDTH_PERC = 0.2;
    private final static double HEIGHT_PERC = 0.1;
    private final static long WAIT_TIME = TimeUnit.SECONDS.toMillis(10);
    
    private final JLabel display = new JLabel();
    private final JButton stop = new JButton("Stop");
    private final JButton up = new JButton("Up");
    private final JButton down = new JButton("Down");
    
    final Agent agent = new Agent();

    /**
     * Builds a new CGUI
     */
    public AnotherConcurrentGUI() {
        super();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() * WIDTH_PERC), (int) (screenSize.getHeight() * HEIGHT_PERC));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        final JPanel panel = new JPanel();
        panel.add(display);
        panel.add(up);
        panel.add(down);
        panel.add(stop);
        this.getContentPane().add(panel);
        this.setVisible(true);
        /*
         * Create the counter
         */ 
        /*
         * Register the listeners
         */
        stop.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(final ActionEvent e) {
                agent.stopCounting();
            }
        });
        up.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(final ActionEvent e) {
                agent.setUp();
            }
        });
        down.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(final ActionEvent e) {
                agent.setDown();
            }
        });
        new Thread(agent).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(WAIT_TIME);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                AnotherConcurrentGUI.this.stopCounting();
            }
        }).start();
    }
    
    private void stopCounting() {
        agent.stopCounting();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                stop.setEnabled(false);
                up.setEnabled(false);
                down.setEnabled(false);
            }
        });
    }
    
    public class Agent implements Runnable {

        private volatile boolean stop;
        private volatile boolean increment = true;
        private int counter;

        @Override
        public void run() {
            while (!this.stop) {
                try {
                        if (this.increment) {
                            this.counter++;
                        } else {
                            this.counter--;
                        }
                        final var next = Integer.toString(this.counter);
                        SwingUtilities.invokeAndWait(new Runnable() {
                            @Override
                            public void run() {
                                AnotherConcurrentGUI.this.display.setText(next);
                            }
                        });
                        Thread.sleep(100);
                } catch (InterruptedException | InvocationTargetException ex) {
                    ex.printStackTrace();
                }
            }
        }
        
        public void stopCounting() {
            this.stop = true;
        }
        
        public void setUp() {
            this.increment = true;
        }
        
        public void setDown() {
            this.increment = false;
        }

    }
}

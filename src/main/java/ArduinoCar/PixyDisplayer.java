package ArduinoCar;

import java.awt.Graphics;
import java.awt.LayoutManager;

import javax.swing.JPanel;

import io.github.pseudoresonance.pixy2api.Pixy2;
import io.github.pseudoresonance.pixy2api.Pixy2Video;

public class PixyDisplayer extends JPanel {

	Pixy2 pixy;
	
	public PixyDisplayer(Pixy2 pixy) {
		this.pixy = pixy;
	}

	public PixyDisplayer(LayoutManager arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public PixyDisplayer(boolean arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public PixyDisplayer(LayoutManager arg0, boolean arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void paint(Graphics g) {
		int w = pixy.getFrameWidth();
		int h = pixy.getFrameHeight();
		Pixy2Video.RGB rgb = new Pixy2Video.RGB(0, 0, 0);
		
		for(int y = 0; y < h; y++) {
			for(int x = 0; x < w; x++) {
				pixy.getVideo().getRGB(x, y, rgb);
				g.setColor(rgb.getColor());
				g.drawRect(x, y, 1, 1);
			}
		}
		
		super.paint(g);
	}

}

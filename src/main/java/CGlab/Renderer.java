package CGlab;


import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Renderer {

    public enum LineAlgo { NAIVE, DDA, BRESENHAM, BRESENHAM_INT; }

    private BufferedImage render;
    public int h = 200;
    public int w = 200;
    public Vec2f vecA;
    public Vec2f vecB;
    public Vec2f vecC;

    public Renderer(String filename, int width, int height, Vec2f vecA, Vec2f vecB, Vec2f vecC) {
        this.h = h;
        this.w = w;
        this.vecA = vecA;
        this.vecB = vecB;
        this.vecC = vecC;
        this.filename = filename;
        //initializeZBuffer();
    }

    public Renderer(String filename,int w, int h) {
        this.h = h;
        this.w = w;
        this.filename = filename;
        render = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        //initializeZBuffer();
    }

    public int getH() {
        return h;
    }

    public int getW() {
        return w;
    }

    private String filename;
    private LineAlgo lineAlgo = LineAlgo.NAIVE;

    public Renderer(String filename) {
        render = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
        this.filename = filename;
    }

    public Renderer(String filename, int width, int height, String algorithm) {
        this.w = width;
        this.h = height;
        this.filename = filename;
        lineAlgo = LineAlgo.valueOf(algorithm);
        render = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        //initializeZBuffer();
    }

    public void drawPoint(int x, int y) {
        int white = 255 | (255 << 8) | (255 << 16) | (255 << 24);
        render.setRGB(x, y, white);
    }

    public void drawLine(int x0, int y0, int x1, int y1, LineAlgo lineAlgo) {
        if(lineAlgo == LineAlgo.NAIVE) drawLineNaive(x0, y0, x1, y1);
        if(lineAlgo == LineAlgo.DDA) drawLineDDA(x0, y0, x1, y1);
        if(lineAlgo == LineAlgo.BRESENHAM) drawLineBresenham(x0, y0, x1, y1);
        if(lineAlgo == LineAlgo.BRESENHAM_INT) drawLineBresenhamInt(x0, y0, x1, y1);
    }

    public void drawLineNaive(int x0, int y0, int x1, int y1) {
        int dx = x1 - x0;
        int dy = y1 - y0;

        int d = dy - (dx / 2);
        int x = x0, y = y0;

        System.out.print(x + "," + y + "\n");

        while (x < x1) {
            x++;

            if (d < 0) {
                d = d + dy;
            } else {
                d += (dy - dx);
                y++;
            }

            System.out.print(x + "," + y + "\n");
            drawPoint(x, y);
        }
    }

    public void drawLineDDA(int x0, int y0, int x1, int y1) {
        // TODO: zaimplementuj
    }

    public void drawLineBresenham(int x0, int y0, int x1, int y1) {
        int white = 255 | (255 << 8) | (255 << 16) | (255 << 24);

        int dx = x1 - x0;
        int dy = y1 - y0;
        float derr = Math.abs(dy / (float) (dx));
        float err = 0;

        int y = y0;

        for (int x = x0; x <= x1; x++) {
            render.setRGB(x, y, white);
            err += derr;
            if (err > 0.5) {
                y += (y1 > y0 ? 1 : -1);
                err -= 1.;
            }
        } // Oktanty: 7, 8
    }

    public void drawLineBresenhamInt(int x0, int y0, int x1, int y1) {
        int white = 255 | (255 << 8) | (255 << 16) | (255 << 24);

        int dx = x1 - x0;
        int dy = y1 - y0;
        int derr = 2 * dy;
        int err = dy - dx;

        int y = y0;
        for (int x = x0; x <= x1; x++) {
            render.setRGB(x, y, white);
            err += derr;
            if (err > 0.5) {
                y += (y1 > y0 ? 1 : -1);
                err -= 2 * dx;
            }

        }// Oktanty: 7, 8
    }

    public void save() throws IOException {
        File outputfile = new File(filename);
        render = Renderer.verticalFlip(render);
        ImageIO.write(render, "png", outputfile);
    }

    public void clear() {
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int black = 0 | (0 << 8) | (0 << 16) | (255 << 24);
                render.setRGB(x, y, black);
            }
        }
    }

    public static BufferedImage verticalFlip(BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage flippedImage = new BufferedImage(w, h, img.getColorModel().getTransparency());
        Graphics2D g = flippedImage.createGraphics();
        g.drawImage(img, 0, 0, w, h, 0, h, w, 0, null);
        g.dispose();
        return flippedImage;
    }
    // LAB 3
    public class Vec2f {
        public float x;
        public float y;

        public Vec2f() {
            x = 0;
            y = 0;
        }

        public Vec2f(float x, float y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return x + " " + y;
        }
    }

    public class Vec3f {
        public float x;
        public float y;
        public float z;

        public Vec3f() {
            x = 0;
            y = 0;
            z = 0;
        }

        public Vec3f(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public void set(int i, float n) {
            if(i == 0) x = n;
            else if(i == 1) y = n;
            else if(i == 2) z = n;
        }

        public float dot(Vec3f b) {
            return (x * b.x + y * b.y + z * b.z);
        }
        public Vec3f cross(Vec3f A) {
            return new Vec3f(y * A.z - z * A.y, z * A.x - x * A.z, x * A.y - y * A.x);
        }

        public float magnitude() {
            return (float)Math.sqrt(x * x + y * y + z * z);
        }
        public Vec3f normalize() {
            float mag = magnitude();

            if(mag != 0) {
                return new Vec3f((x / mag), (y / mag), (z / mag));
            } else return new Vec3f(0, 0, 0);
        }

        @Override
        public String toString() {
            return x + " " + y + " " + z;
        }
    }

    public class Vec2i {
        public int x;
        public int y;

        public Vec2i() {
            x = 0;
            y = 0;
        }

        public Vec2i(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return x + " " + y;
        }
    }

    public class Vec3i {
        public int x;
        public int y;
        public int z;

        public Vec3i(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public int get(int i) {
            if(i == 0) return x;
            else if(i == 1) return y;
            else if(i == 2) return z;
            else return 0;
        }

        @Override
        public String toString() {
            return x + " " + y + " " + z;
        }
    }

    public Vec3f barycentric(Vec2i A, Vec2i B, Vec2i C, Vec2i P) {
        Vec3f v1 = new Vec3f(B.x - A.x, C.x - A.x, A.x - P.x);
        Vec3f v2 = new Vec3f(B.y - A.y, C.y - A.y, A.y - P.y);

        Vec3f cross = v1.cross(v2);
        Vec2f uv = new Vec2f(cross.x / cross.z, cross.y / cross.z);

        return new Vec3f(uv.x, uv.y, 1 - uv.x - uv.y);
    }

//    public void drawTriangle2(Vec2i A, Vec2i B, Vec2i C, int rgb, Vec3f world_z) {
//        int bb_lx = Math.min(Math.min(A.x, B.x), C.x);
//        int bb_ly = Math.min(Math.min(A.y, B.y), C.y);
//        int bb_hx = Math.max(Math.max(A.x, B.x), C.x);
//        int bb_hy = Math.max(Math.max(A.y, B.y), C.y);
//
//        for(int y = bb_ly; y < bb_hy; ++y) {
//            for(int x = bb_lx; x < bb_hx; ++x) {
//                Vec3f result = barycentric(A, B, C, new Vec2i(x, y));
//
//                if(result.x >= 0 && result.x <= 1 && result.y >= 0 && result.y <= 1 && result.z >= 0 && result.z <= 1) {
//                    float z = result.x * world_z.x + result.y * world_z.y + result.z * world_z.z;
//                    if(z < zBuffer.get(y * render.getWidth() + x)) {
//                        render.setRGB(x, y, rgb);
//                        zBuffer.set(y * render.getWidth() + x, z);
//                    }
//                }
//            }
//        }
//    }
}

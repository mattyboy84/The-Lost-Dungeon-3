package root.game.player;

import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.shape.Rectangle;
import root.game.util.Vecc2f;

public class DungeonMap {

    mapPiece[][] mapPieces;
    ImageView border = null;
    Vecc2f borderPos, borderPosCenter;
    float scaleX, scaleY;
    double sheetScale;
    Rectangle[] edges = new Rectangle[4];

    public DungeonMap(String file, int width, int height, int visitedX, int visitedY, int unvisitedX, int unvisitedY, int currentX, int currentY, int[][] map, float scaleX, float scaleY, Rectangle2D screenBounds, double sheetScale) {
        mapPieces = new mapPiece[map.length][map[0].length];
        for (int i = 0; i < mapPieces.length; i++) {
            for (int j = 0; j < mapPieces[0].length; j++) {
                if (map[i][j] > 0) {
                    mapPieces[i][j] = new mapPiece(i, j, file, width, height, visitedX, visitedY, unvisitedX, unvisitedY, currentX, currentY, scaleX, scaleY, screenBounds, sheetScale, map[i][j]);
                }
            }
        }
        if (file.contains("minimap1")) {
            this.border = (new ImageView(new WritableImage(new Image(file, (new Image(file).getWidth() * scaleX * sheetScale),
                    (new Image(file).getHeight() * scaleY * sheetScale), false, false).getPixelReader(),
                    (int) 0, (int) 0, (int) (53 * scaleX * sheetScale), (int) (47 * scaleY * sheetScale))));
            this.borderPos = new Vecc2f(screenBounds.getWidth() - this.border.getBoundsInParent().getWidth(), 0);
            this.border.relocate(this.borderPos.x, this.borderPos.y);
            this.borderPosCenter = new Vecc2f(this.borderPos.x + (this.border.getBoundsInParent().getWidth() / 2), this.borderPos.y + (this.border.getBoundsInParent().getHeight() / 2));

            edges[0] = new Rectangle(this.border.getBoundsInParent().getMinX(), this.border.getBoundsInParent().getMinY(), this.border.getBoundsInParent().getWidth(), 1);//top
            edges[1] = new Rectangle(this.border.getBoundsInParent().getMinX(), this.border.getBoundsInParent().getMaxY(), this.border.getBoundsInParent().getWidth(), 1);//bottom
            edges[2] = new Rectangle(this.border.getBoundsInParent().getMinX(), this.border.getBoundsInParent().getMinY(), 1, this.border.getBoundsInParent().getHeight());//left
            edges[3] = new Rectangle(this.border.getBoundsInParent().getMaxX(), this.border.getBoundsInParent().getMinY(), 1, this.border.getBoundsInParent().getHeight());//right


        }
        //
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.sheetScale = sheetScale;

    }

    public void updateMinimap(int X, int Y) {
        Vecc2f a = new Vecc2f();
        a.set(borderPosCenter);
        a.sub(mapPieces[X][Y].position);

        for (DungeonMap.mapPiece[] mapPiece : mapPieces) {
            for (int l = 0; l < mapPieces[0].length; l++) {
                if (mapPiece[l] != null && mapPiece[l].alt) {//corrections for altered pieces that have been cut off by the border
                    mapPiece[l].position.set(mapPiece[l].altPos.x, mapPiece[l].altPos.y);
                    mapPiece[l].alt = false;
                }
            }
        }

        for (DungeonMap.mapPiece[] mapPiece : mapPieces) {
            for (int l = 0; l < mapPieces[0].length; l++) {
                if (mapPiece[l] != null) {
                    mapPiece[l].updatePos((int) ((mapPiece[l].position.x + a.x) - (mapPiece[l].width / 2)), (int) ((mapPiece[l].position.y + a.y) - (mapPiece[l].height / 2)));
                }
            }
        }
        update(X, Y);
    }

    public void updateLargemap(int X, int Y, Rectangle2D screenBounds) {
        update(X, Y);
        //
        int maxX = 0;
        int minY = Integer.MAX_VALUE;

        for (DungeonMap.mapPiece[] mapPiece : mapPieces) {
            for (int j = 0; j < mapPieces[0].length; j++) {
                if (mapPiece[j] != null && mapPiece[j].piece.isVisible()) {
                    if ((mapPiece[j].position.x + mapPiece[j].piece.getBoundsInParent().getWidth()) > maxX) {
                        maxX = (int) (mapPiece[j].position.x + mapPiece[j].piece.getBoundsInParent().getWidth());
                    }
                    if ((mapPiece[j].position.y) < minY) {
                        minY = (int) (mapPiece[j].position.y);
                    }
                }
            }
        }

        for (DungeonMap.mapPiece[] mapPiece : mapPieces) {
            for (int j = 0; j < mapPieces[0].length; j++) {
                if (mapPiece[j] != null) {
                    mapPiece[j].updatePos(mapPiece[j].position.x+((screenBounds.getWidth()) - maxX),mapPiece[j].position.y + -minY);
                }
            }
        }

    }

    private void update(int X, int Y) {
        seenChecker(X + 0, Y + 0);
        seenChecker(X + 0, Y + 1);
        seenChecker(X + 1, Y + 0);
        seenChecker(X + 0, Y + -1);
        seenChecker(X + -1, Y + 0);

        mapPieces[X][Y].visited = true;

        for (DungeonMap.mapPiece[] mapPiece : mapPieces) {
            for (int j = 0; j < mapPieces[0].length; j++) {
                if (mapPiece[j] != null) {
                    mapPiece[j].updateVis(false);
                    mapPiece[j].current = false;
                    if (mapPiece[j].seen) {
                        mapPiece[j].piece.setImage(mapPiece[j].unvisited_piece);
                        mapPiece[j].updateVis(true);
                    }
                    if (mapPiece[j].visited) {
                        mapPiece[j].piece.setImage(mapPiece[j].visited_piece);
                        mapPiece[j].updateVis(true);
                    }
                    if (edges[0] != null) {
                        for (int i = 0; i < edges.length; i++) {
                            if (mapPiece[j].piece.getBoundsInParent().intersects(edges[i].getBoundsInParent())) {
                                switch (i) {
                                    case 1 ->//bottom
                                            mapPiece[j].cutOffBottom(edges[i].getBoundsInParent());
                                    case 2 ->//left
                                            mapPiece[j].cutOffLeft(edges[2].getBoundsInParent());
                                }
                                mapPiece[j].iconCheck(false);
                            }
                        }
                    }
                }
            }
        }
        //
        if (this.border != null) {
            for (DungeonMap.mapPiece[] piece : mapPieces) {
                for (int j = 0; j < mapPieces[0].length; j++) {
                    if (piece[j] != null) {
                        if (!(piece[j].piece.getBoundsInParent().intersects(this.border.getBoundsInParent()))) {
                            piece[j].updateVis(false);
                        }
                    }
                }
            }
        }
        //
        mapPieces[X][Y].current = true;
        mapPieces[X][Y].piece.setImage(mapPieces[X][Y].current_piece);
        mapPieces[X][Y].piece.setVisible(true);
        if (mapPieces[X][Y].icon != null) {
            mapPieces[X][Y].icon.setVisible(true);
        }
    }

    private void seenChecker(int i, int i1) {
        try {
            mapPieces[i][i1].seen = true;
        } catch (Exception e) {

        }
    }

    public void load(Group group) {
        if (this.border != null) {
            group.getChildren().add(this.border);
            this.border.relocate(this.borderPos.x, this.borderPos.y);
            this.border.setViewOrder(-12);
        }
        for (int i = 0; i < mapPieces.length; i++) {
            for (int j = 0; j < mapPieces[0].length; j++) {
                if (mapPieces[i][j] != null) {
                    mapPieces[i][j].load(group);
                }
            }
        }

    }


    public void unload(Group group) {
        if (this.border != null) {
            group.getChildren().remove(this.border);
        }
        for (int i = 0; i < mapPieces.length; i++) {
            for (int j = 0; j < mapPieces[0].length; j++) {
                if (mapPieces[i][j] != null) {
                    mapPieces[i][j].unload(group);
                }
            }
        }
    }

    public void reveal() {
        for (int i = 0; i < mapPieces.length; i++) {
            for (int j = 0; j < mapPieces[0].length; j++) {
                if (mapPieces[i][j] != null) {
                    mapPieces[i][j].seen = true;
                }
            }
        }
    }


    private class mapPiece {

        ImageView piece = new ImageView();
        ImageView icon = new ImageView();
        Image visited_piece, unvisited_piece, current_piece;
        Vecc2f position;
        int width;
        int height;
        boolean alt = false;
        Vecc2f altPos;

        boolean seen = false, visited = false, current = false;

        public mapPiece(int j, int i, String file, int width, int height, int visitedX, int visitedY, int unvisitedX, int unvisitedY, int currentX, int currentY, float scaleX, float scaleY, Rectangle2D screenBounds, double sheetScale, int type) {
            //
            this.width = (int) (width * scaleX * sheetScale);
            this.height = (int) (height * scaleY * sheetScale);

            visited_piece = (new ImageView(new WritableImage(new Image(file, (new Image(file).getWidth() * scaleX * sheetScale),
                    (new Image(file).getHeight() * scaleY * sheetScale), false, false).getPixelReader(),
                    (int) (visitedX * scaleX * sheetScale), (int) (visitedY * scaleY * sheetScale), (int) (width * scaleX * sheetScale), (int) (height * scaleY * sheetScale))).getImage());
            unvisited_piece = (new ImageView(new WritableImage(new Image(file, (new Image(file).getWidth() * scaleX * sheetScale),
                    (new Image(file).getHeight() * scaleY * sheetScale), false, false).getPixelReader(),
                    (int) (unvisitedX * scaleX * sheetScale), (int) (unvisitedY * scaleY * sheetScale), (int) (width * scaleX * sheetScale), (int) (height * scaleY * sheetScale))).getImage());
            current_piece = (new ImageView(new WritableImage(new Image(file, (new Image(file).getWidth() * scaleX * sheetScale),
                    (new Image(file).getHeight() * scaleY * sheetScale), false, false).getPixelReader(),
                    (int) (currentX * scaleX * sheetScale), (int) (currentY * scaleY * sheetScale), (int) (width * scaleX * sheetScale), (int) (height * scaleY * sheetScale))).getImage());
            //
            this.piece.setImage(unvisited_piece);

            position = new Vecc2f(i * (width * scaleX * sheetScale), j * (height * scaleY * sheetScale));

            scaleX = (float) (scaleX * (this.piece.getBoundsInParent().getWidth() / 16));
            scaleY = (float) (scaleY * (this.piece.getBoundsInParent().getHeight() / 16));//scales the icon sheet so that the icons are same width & height as their room

            if (type > 1) {
                switch (type) {
                    case 2 ->//shop
                            iconGetter(scaleX, scaleY, 64, 0);
                    case 3 ->//boss
                            iconGetter(scaleX, scaleY, 0, 16);
                }
            }
        }

        private void iconGetter(float scaleX, float scaleY, int startX, int startY) {
            this.icon = (new ImageView(new WritableImage(new Image("file:src\\resources\\gfx\\ui\\minimap_icons.png", (new Image("file:src\\resources\\gfx\\ui\\minimap_icons.png").getWidth() * scaleX),
                    (new Image("file:src\\resources\\gfx\\ui\\minimap_icons.png").getHeight() * scaleY), false, false).getPixelReader(),
                    (int) (startX * scaleX), (int) (startY * scaleY), (int) ((16 * scaleX)), (int) (16 * scaleY))));
        }

        public void load(Group group) {
            group.getChildren().addAll(this.piece);
            this.piece.relocate(this.position.x, this.position.y);
            this.piece.setViewOrder(-11);

            if (this.icon != null) {
                group.getChildren().add(this.icon);
                this.icon.relocate(this.position.x, this.position.y);
                this.icon.setViewOrder(-11);
            }
        }

        public void updatePos(double v, double v1) {
            this.position.set(v, v1);
            this.piece.relocate(this.position.x, this.position.y);
            if (this.icon != null) {
                this.icon.relocate(this.position.x, this.position.y);
            }
        }

        public void unload(Group group) {
            group.getChildren().remove(this.piece);
            if (this.icon != null) {
                group.getChildren().remove(this.icon);
            }
        }

        public void cutOffBottom(Bounds edge) {

            int as = (int) Math.abs(this.piece.getBoundsInParent().getMaxY() - edge.getMaxY()) + 1;

            this.piece.setImage((new ImageView(new WritableImage(this.piece.getImage().getPixelReader(),
                    (int) (0), (int) (0), (int) (this.piece.getBoundsInParent().getWidth()), (int) (this.piece.getBoundsInParent().getHeight() - as))).getImage()));
        }

        public void cutOffLeft(Bounds edge) {
            int as = (int) Math.abs(this.piece.getBoundsInParent().getMinX() - edge.getMinX()) + 1;
            this.piece.setImage((new ImageView(new WritableImage(this.piece.getImage().getPixelReader(),
                    (int) (as), (int) (0), (int) (this.piece.getBoundsInParent().getWidth() - as), (int) (this.piece.getBoundsInParent().getHeight()))).getImage()));
            this.alt = true;
            this.altPos = new Vecc2f(this.position);
            this.position.add(as, 0);
            this.piece.relocate(this.position.x, this.position.y);

        }

        public void iconCheck(boolean b) {
            if (icon != null) {
                icon.setVisible(b);
            }
        }

        public void updateVis(boolean b) {
            this.piece.setVisible(b);
            iconCheck(b);
        }
    }
}
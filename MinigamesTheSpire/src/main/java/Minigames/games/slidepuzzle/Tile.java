package Minigames.games.slidepuzzle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;

public class Tile {
    //tile variables
    public boolean isEmpty = false;
    public TextureRegion region;

    //border graphics
    private static final Texture TILE_BORDER = ImageMaster.loadImage("minigamesResources/img/games/slidepuzzle/SlidePuzzleTileBorder.png");
    private static final TextureRegion TILE_BORDER_REGION = new TextureRegion(TILE_BORDER);
    private static final Color MOVING_COLOR = Color.WHITE.cpy();
    private static final Color SOLVED_COLOR = Color.CYAN.cpy();
    private static final Color UNSOLVED_COLOR = Color.FIREBRICK.cpy();

    //slide variables
    private static final float SLIDE_TIME = 0.15f; //in seconds
    private float slideTimer = 0.0f;

    //main minigame control variables
    public SlidePuzzleMinigame parent;
    public Vector2 gridPosition;
    public Vector2 solvePosition;

    //tile animation variables
    private static final float ROTATION_SPEED = 720.0f; //degrees per second
    private Vector2 currentPosition;
    private Vector2 previousPosition;
    private Vector2 targetPosition;
    private float rotation = 0.0f;
    private float movementTimer = 0.0f;
    private float movementTime = 0.0f;
    private float startRotation = 0.0f;
    private float rotationDirection = 1.0f;

    public Tile(Vector2 position, SlidePuzzleMinigame parent) {
        gridPosition = position.cpy();
        solvePosition = position.cpy();
        currentPosition = gridPosition.cpy();
        previousPosition = gridPosition.cpy();
        targetPosition = gridPosition.cpy();
        this.parent = parent;
    }

    //when tile is clicked, determine if it's next to the empty tile. If it is, slide it over.
    public void clicked(Tile[][] board) {
        if (isEmpty) {
            return;
        }
        Tile emptyTile = null;
        for (Tile[] row : board) {
            for (Tile tile : row) {
                if (tile.isEmpty) {
                    emptyTile = tile;
                    break;
                }
            }
        }
        if (isNeighbor(emptyTile)) {
            parent.sliding = true; //disable clicks during sliding animation
            CardCrawlGame.sound.play("MAP_SELECT_3");
            swap(emptyTile);
        }
    }

    //exchanges the grid positions of two tiles, and sets them to animate a short sliding animation to their new positions.
    //technically this should never be called between two non-empty tiles.
    private void swap(Tile otherTile) {
        Vector2 swapAlgorithm = otherTile.gridPosition;
        otherTile.gridPosition = gridPosition.cpy();
        gridPosition = swapAlgorithm.cpy();
        previousPosition = currentPosition.cpy();
        otherTile.previousPosition = otherTile.currentPosition.cpy();
        slideTimer = otherTile.slideTimer = SLIDE_TIME;
    }

    //initializes any movement
    public void setMovement(Vector2 target, float duration) {
        previousPosition = currentPosition.cpy();
        targetPosition = target.cpy();
        movementTime = movementTimer = duration;
        startRotation = rotation;
        if (AbstractDungeon.miscRng.randomBoolean()) {
            rotationDirection *= -1;
        }
    }

    //move towards generated positions during opening animation
    public void updateInitialize(float elapsed) {
        movementTime -= elapsed;
        currentPosition.x = Interpolation.linear.apply(targetPosition.x, previousPosition.x, movementTime / movementTimer);
        currentPosition.y = Interpolation.linear.apply(targetPosition.y, previousPosition.y, movementTime / movementTimer);
        if (targetPosition.equals(gridPosition)) {
            rotation = Interpolation.linear.apply(0.0f, startRotation, movementTime / movementTimer);
        } else {
            rotation += ROTATION_SPEED * elapsed * rotationDirection;
            if (rotation < 0.0f) {
                rotation += 360.0f;
            }
            if (rotation > 360.0f) {
                rotation -= 360.0f;
            }
        }
    }

    public void updatePlay(float elapsed) {
        //no need to do fancy stuff for empty tile
        if (isEmpty) {
            currentPosition = gridPosition.cpy();
            previousPosition = gridPosition.cpy();
            return;
        }

        //if sliding, move towards new position
        if (slideTimer > 0.0f) {
            slideTimer -= elapsed;
            if (slideTimer <= 0.0f) {
                slideTimer = 0.0f;
                currentPosition = gridPosition.cpy();
                previousPosition = gridPosition.cpy();
                parent.sliding = false; //once sliding is finished, allow clicking again
                CardCrawlGame.sound.play("UI_CLICK_1");
            } else {
                currentPosition.x = Interpolation.linear.apply(gridPosition.x, previousPosition.x, slideTimer / SLIDE_TIME);
                currentPosition.y = Interpolation.linear.apply(gridPosition.y, previousPosition.y, slideTimer / SLIDE_TIME);
            }
        } else {
            currentPosition = gridPosition.cpy();
            rotation = 0.0f;
        }
    }

    //move and rotate towards randomly generated explosion position
    public void updateDefeat(float elapsed) {
        movementTime -= elapsed;
        currentPosition.x = Interpolation.linear.apply(targetPosition.x, previousPosition.x, movementTime / movementTimer);
        currentPosition.y = Interpolation.linear.apply(targetPosition.y, previousPosition.y, movementTime / movementTimer);
        rotation += ROTATION_SPEED * elapsed * rotationDirection * 0.1f;
        if (rotation < 0.0f) {
            rotation += 360.0f;
        }
        if (rotation > 360.0f) {
            rotation -= 360.0f;
        }
    }

    //tests if this tile is currently adjacent to other tile, but not across corners
    private boolean isNeighbor(Tile otherTile) {
        if (otherTile != null) {
            return ((otherTile.gridPosition.x == gridPosition.x + 1 && otherTile.gridPosition.y == gridPosition.y) ||
                    (otherTile.gridPosition.x == gridPosition.x - 1 && otherTile.gridPosition.y == gridPosition.y) ||
                    (otherTile.gridPosition.y == gridPosition.y + 1 && otherTile.gridPosition.x == gridPosition.x) ||
                    (otherTile.gridPosition.y == gridPosition.y - 1 && otherTile.gridPosition.x == gridPosition.x));
        } else return false;
    }

    public void render(SpriteBatch sb) {
        //no rendering empty tiles
        if (isEmpty) {
            return;
        }

        //find real game coordinates
        Vector2 renderPosition = convertToRealPosition(currentPosition);

        //use the tile's size for both renders
        float width = region.getRegionWidth();
        float height = region.getRegionHeight();

        //render the tile
        sb.setColor(Color.WHITE.cpy());
        sb.draw(region, renderPosition.x - width / 2.0f, renderPosition.y - height / 2.0f, width / 2.0f, height / 2.0f, width, height, Settings.scale, Settings.scale, rotation);

        //set the border color according to status
        if (parent.victoryColor != null) {
            sb.setColor(parent.victoryColor);
        } else if (!currentPosition.equals(gridPosition)) {
            sb.setColor(MOVING_COLOR);
        } else if (gridPosition.equals(solvePosition)) {
            sb.setColor(SOLVED_COLOR);
        } else {
            sb.setColor(UNSOLVED_COLOR);
        }

        //render the tile border
        sb.draw(TILE_BORDER_REGION, renderPosition.x - width / 2.0f, renderPosition.y - height / 2.0f, width / 2.0f, height / 2.0f, width, height, Settings.scale, Settings.scale, rotation);
    }

    //converts a coordinate relative to the game grid to a real game coordinate
    private Vector2 convertToRealPosition(Vector2 coordinate) {
        Vector2 retVal = coordinate.cpy();
        retVal.x *= parent.TILE_SIZE;
        retVal.y *= parent.TILE_SIZE;
        retVal.x += Settings.WIDTH / 2.0f;
        retVal.y += Settings.HEIGHT / 2.0f;
        retVal.x -= (parent.TILE_SIZE * parent.BOARD_SIZE) / 2.0f;
        retVal.y -= (parent.TILE_SIZE * parent.BOARD_SIZE) / 2.0f;
        retVal.x += parent.TILE_SIZE / 2.0f;
        retVal.y += parent.TILE_SIZE / 2.0f;
        return retVal;
    }
}

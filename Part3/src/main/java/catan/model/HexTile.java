package catan.model;

import catan.enums.TileType;

/**
 * Represents a hexagonal terrain tile on the Catan board.
 */
public class HexTile {
    private final int tileID;
    private final TileType type;
    private final int numberToken;

    public HexTile(int tileID, TileType type, int numberToken) {
        this.tileID = tileID;
        this.type = type;
        this.numberToken = numberToken;
    }

    public int getTileID() {
        return tileID;
    }

    public TileType getType() {
        return type;
    }

    public int getNumberToken() {
        return numberToken;
    }

    /**
     * @return true if this tile has a valid number token (non-desert)
     */
    public boolean hasNumberToken() {
        return numberToken > 0;
    }
}

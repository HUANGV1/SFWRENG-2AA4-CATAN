package catan.enums;

/**
 * Terrain types for hex tiles on the Catan board.
 * Each terrain (except DESERT) produces a specific resource.
 */
public enum TileType {
    WOOD,
    BRICK,
    WHEAT,
    SHEEP,
    ORE,
    DESERT;

    /**
     * Maps this tile type to the resource it produces.
     * 
     * @return the corresponding ResourceType, or null for DESERT
     */
    public ResourceType toResourceType() {
        return switch (this) {
            case WOOD -> ResourceType.LUMBER;
            case BRICK -> ResourceType.BRICK;
            case WHEAT -> ResourceType.GRAIN;
            case SHEEP -> ResourceType.WOOL;
            case ORE -> ResourceType.ORE;
            case DESERT -> null;
        };
    }
}

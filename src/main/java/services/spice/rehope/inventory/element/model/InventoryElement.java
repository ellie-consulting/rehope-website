package services.spice.rehope.inventory.element.model;

/**
 * Represents an element that can be placed in an inventory.
 *
 * @param id Database id.
 * @param elementId String id.
 * @param type Type.
 * @param rarity Rarity.
 * @param name Pretty name.
 * @param description Description.
 * @param iconUri Uri to icon.
 */
public record InventoryElement(int id, String elementId, ElementType type, ElementRarity rarity,
                               String name, String description, String iconUri) {

}

import Cocoa

func colorToHex( color: NSColor ) -> String {
    return String( format: (color.alphaComponent != 1 ? "#%02x%02x%02x%02x" : "#%02x%02x%02x"),
                  Int( 255 * color.redComponent ),
                  Int( 255 * color.greenComponent ),
                  Int( 255 * color.blueComponent ),
                  Int( 255 * color.alphaComponent ) )
}

func printColorHex( color: NSColor, space: NSColorSpace ) {
    print( "@ns", color.colorNameComponent.prefix(1).capitalized, color.colorNameComponent.dropFirst(),
           " = ", colorToHex( color: color.usingColorSpace( space )! ),
           separator: "" )
}

func printColorsHex( space: NSColorSpace ) {
    print( "#----", space, "----" )
    
    // order is the same as in Xcode color chooser (Color Palettes > Developer)

    printColorHex( color: NSColor.labelColor, space: space )
    printColorHex( color: NSColor.secondaryLabelColor, space: space )
    printColorHex( color: NSColor.tertiaryLabelColor, space: space )
    printColorHex( color: NSColor.quaternaryLabelColor, space: space )

    printColorHex( color: NSColor.systemRed, space: space )
    printColorHex( color: NSColor.systemGreen, space: space )
    printColorHex( color: NSColor.systemBlue, space: space )
    printColorHex( color: NSColor.systemOrange, space: space )
    printColorHex( color: NSColor.systemYellow, space: space )
    printColorHex( color: NSColor.systemBrown, space: space )
    printColorHex( color: NSColor.systemPink, space: space )
    printColorHex( color: NSColor.systemPurple, space: space )
    printColorHex( color: NSColor.systemTeal, space: space )
    printColorHex( color: NSColor.systemIndigo, space: space )
    printColorHex( color: NSColor.systemMint, space: space )
    printColorHex( color: NSColor.systemCyan, space: space )
    printColorHex( color: NSColor.systemGray, space: space )

    printColorHex( color: NSColor.linkColor, space: space )
    printColorHex( color: NSColor.placeholderTextColor, space: space )
    printColorHex( color: NSColor.windowFrameTextColor, space: space )
    printColorHex( color: NSColor.selectedMenuItemTextColor, space: space )
    printColorHex( color: NSColor.alternateSelectedControlTextColor, space: space )
    printColorHex( color: NSColor.headerTextColor, space: space )

    printColorHex( color: NSColor.separatorColor, space: space )
    printColorHex( color: NSColor.gridColor, space: space )

    printColorHex( color: NSColor.textColor, space: space )
    printColorHex( color: NSColor.textBackgroundColor, space: space )
    printColorHex( color: NSColor.selectedTextColor, space: space )
    printColorHex( color: NSColor.selectedTextBackgroundColor, space: space )
    printColorHex( color: NSColor.unemphasizedSelectedTextBackgroundColor, space: space )
    printColorHex( color: NSColor.unemphasizedSelectedTextColor, space: space )

    printColorHex( color: NSColor.windowBackgroundColor, space: space )
    printColorHex( color: NSColor.underPageBackgroundColor, space: space )
    printColorHex( color: NSColor.controlBackgroundColor, space: space )

    printColorHex( color: NSColor.selectedContentBackgroundColor, space: space )
    printColorHex( color: NSColor.unemphasizedSelectedContentBackgroundColor, space: space )
    print( "# alternatingContentBackgroundColors =", NSColor.alternatingContentBackgroundColors )

    printColorHex( color: NSColor.findHighlightColor, space: space )

    printColorHex( color: NSColor.controlColor, space: space )
    printColorHex( color: NSColor.controlTextColor, space: space )
    printColorHex( color: NSColor.selectedControlColor, space: space )
    printColorHex( color: NSColor.selectedControlTextColor, space: space )
    printColorHex( color: NSColor.disabledControlTextColor, space: space )

    printColorHex( color: NSColor.keyboardFocusIndicatorColor, space: space )
    printColorHex( color: NSColor.controlAccentColor, space: space )

    print()
}

// printColorsHex(  space: NSColorSpace.genericRGB )
printColorsHex(  space: NSColorSpace.deviceRGB )

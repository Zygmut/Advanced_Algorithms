package utils;

/**
 * This class contains some useful methods to be used in the project, for
 * construction of paths, etc.
 */
public class Helpers {

    /**
     * This method returns the build path to the asset with the given name.
     * 
     * @param assetName The name of the asset.
     * @return The path to the asset.
     */
    public static String getAssetPath(String assetName) {
        StringBuilder sb = new StringBuilder();
        sb.append(Config.PATH_TO_ASSETS);
        sb.append(assetName);
        sb.append(Config.ASSET_EXTENSION_OF_PIECES);
        return sb.toString();
    }

}

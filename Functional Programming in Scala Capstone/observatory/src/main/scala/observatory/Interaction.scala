package observatory

import com.sksamuel.scrimage.{Image}

/**
  * 3rd milestone: interactive visualization
  */
object Interaction extends InteractionInterface {

  /**
    * @param tile Tile coordinates
    * @return The latitude and longitude of the top-left corner of the tile, as per http://wiki.openstreetmap.org/wiki/Slippy_map_tilenames
    */
  def tileLocation(tile: Tile): Location = tile.toLocation

  /**
    * @param temperatures Known temperatures
    * @param colors Color scale
    * @param tile Tile coordinates
    * @return A 256×256 image showing the contents of the given tile
    */
  def tile(temperatures: Iterable[(Location, Temperature)], colors: Iterable[(Temperature, Color)], tile: Tile): Image = 
    Visualization.visualize(tile.subTiles(8).map(_.toLocation), temperatures,colors, 256, 256, 127)

  /**
    * Generates all the tiles for zoom levels 0 to 3 (included), for all the given years.
    * @param yearlyData Sequence of (year, data), where `data` is some data associated with
    *                   `year`. The type of `data` can be anything.
    * @param generateImage Function that generates an image given a year, a zoom level, the x and
    *                      y coordinates of the tile and the data to build the image from
    */
  def generateTiles[Data](
    yearlyData: Iterable[(Year, Data)],
    generateImage: (Year, Tile, Data) => Unit
  ): Unit = {
    val tuples: Iterable[(Year, Tile, Data)] = for{
      (year, data) <- yearlyData;
      zoom <- 0 to 3;
      tile <- Tile(0, 0, 0).subTiles(zoom)
    } yield (year, tile, data)

    tuples.par.foreach(x => generateImage(x._1, x._2, x._3))
  }

}

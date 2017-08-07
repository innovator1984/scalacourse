package observatory.utils

import java.nio.file.Paths

/**
  * Created by user on 8/7/17.
  */
object MyResources {
  def resourcePath(resource: String): String =
    Paths.get(getClass.getResource(resource).toURI).toString
}

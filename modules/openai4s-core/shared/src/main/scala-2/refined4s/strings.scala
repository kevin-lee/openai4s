package refined4s

import eu.timepit.refined
import eu.timepit.refined.api.{Refined, RefinedTypeOps}

object strings {
  type Uri = String Refined refined.string.Uri

  object Uri extends RefinedTypeOps[Uri, String]

}

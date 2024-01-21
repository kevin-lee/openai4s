package openai4s.compat

import refined4s.compat.RefinedCompatAllTypes

/** @author Kevin Lee
  * @since 2023-04-24
  */
trait TypesCompat extends RefinedCompatAllTypes with refined4s.syntax {

  val CommonGens = hedgehog.extra.Gens

  val RefinedNumGens = hedgehog.extra.refined4s.gens.NumGens
  val StringGens     = hedgehog.extra.refined4s.gens.StringGens
  val NetGens        = hedgehog.extra.refined4s.gens.NetworkGens

}

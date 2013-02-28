package somePackage

object Hello extends IncreasesNumbers {
  def main(args: Array[String]) {
    println(increase(3))
  }

  def increase(number:Int) = {
    number + 1
  }
}

trait IncreasesNumbers {
  def increase(number:Int) : Int
}

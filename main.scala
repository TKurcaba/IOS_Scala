import scala.App

import scala.io.Source


object Main {
  def main(args: Array[String]): Unit = {

    if (args.length == 0) {
      println("You must specify exactly 2 arguments: the file path and the pattern you are looking for.")
      return
    }
    //open file
    val source = Source.fromFile(args(0))
    val list_of_keys = args(1).split('.')

    // remove all whitespace except between quotation marks
    val FileArray = source.getLines.map(line => line.replaceAll("(?<!(\").{0,255}) | (?!.*\\1.*)", "")).toList
    var n_line = 0

    var res = ""
    //Loop for merge string, into one long string without unnecessary whitespaces
    while (n_line != FileArray.size) {
      val what_found = Extractors.whitespace(FileArray(n_line))
      var temp = ""
      what_found match {
        case f: Found => temp = f.rest
        case _ => temp = FileArray(n_line)
      }
      res = res + temp
      n_line = n_line + 1
    }

    //A recursive function that checks if a given pattern exists in the file
    Checking(res, list_of_keys)
    source.close()
  }


  def Checking(first: String, array: Array[String], HowMuchSuits: Int = 0,deep:Int=1 ): Unit ={
    // Using Extractors object to find keys
    Extractors.key(first) match {
      case f: Found => {// if we find the key we will check "=" because each object is followed by a =
        Extractors.symbol('=')(f.rest) match{
          case f2: Found => {  //next, we check whether it is object or value
              Extractors.symbol('[')(f2.rest) match{
                case f3: Found => { // if there is [, We check whether we have found a solution
                  if((f.extracted == array(HowMuchSuits))&&(deep-1 == HowMuchSuits)){
                    if(deep == array.length){
                      println("Object")
                      return
                    } else Checking(f3.rest, array, HowMuchSuits+1,deep+1 )  // if we find one element of pattern we increment deep and HowMuchSuit
                  }else { Checking(f3.rest, array,HowMuchSuits,deep+1)} // if not we increment only deep, because we still "go deeper"
                }
                case n3: _ => {
                    Extractors.quoted(f2.rest) match { // now we try to check whether it is a value or not
                      case f4: Found =>{
                        if((deep== array.length)&& (f.extracted == array(HowMuchSuits))) { // if so, we print
                          println("Value(" + f4.extracted + ")")
                          return;
                        }
                        else{
                          Extractors.symbol(']')(f4.rest)  match{ // if not, we check whether it is end of one of objects
                            case f5: Found => {
                              if(deep>0) // we have to check that we are in object, if not we restart our variables
                                Checking(f5.rest, array, HowMuchSuits,deep-1 )
                                else{
                                Checking(f5.rest, array, 0,0 )
                              }

                            } // iif the object is not finished, we search further
                            case n5: _ => Checking(f4.rest, array, HowMuchSuits,deep )
                          }

                        }
                      }
                    }
                }
              }
          }
          case n2: _=> println("None"); return;
        }
      }
      case _ => println("None"); return;
    }
  }
}


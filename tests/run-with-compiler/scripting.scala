object Test {
  def main(args: Array[String]): Unit = {
//  val m = new javax.script.ScriptEngineManager()
//  val e = m.getEngineByName("scala")
    val f = new dotty.tools.repl.ScriptEngine.Factory()
    val e = f.getScriptEngine()
    println(e.eval("42"))
  }
}


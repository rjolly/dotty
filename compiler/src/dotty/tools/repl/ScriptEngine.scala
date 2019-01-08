package dotty.tools
package repl

import java.io.{Reader, StringWriter};
import javax.script.{AbstractScriptEngine, Bindings, ScriptContext, ScriptEngine => JScriptEngine, ScriptEngineFactory, ScriptException, SimpleBindings}

class ScriptEngine(factory: ScriptEngine.Factory) extends AbstractScriptEngine {
  val driver = factory.driver
  val rendering = new Rendering
  var state: State = driver.initialState

  def getFactory(): ScriptEngineFactory = factory

  def createBindings: Bindings = new SimpleBindings

  /* Evaluate with the given context. */
  @throws[ScriptException]
  def eval(script: String, context: ScriptContext): Object = {
    val vid = state.valIndex
    state = driver.run(script)(state)
    val oid = state.objectIndex
    Class.forName(s"rs$$line$$$oid", true, rendering.classLoader()(state.context))
      .getDeclaredMethods.find(_.getName == s"res$vid")
      .map(_.invoke(null))
      .getOrElse(null)
  }

  @throws[ScriptException]
  def eval(reader: Reader, context: ScriptContext): Object = eval(stringFromReader(reader), context)

  def stringFromReader(reader: Reader) = {
    val writer = new StringWriter()
    var c = reader.read()
    while(c != -1) {
      writer.write(c)
      c = reader.read()
    }
    reader.close()
    writer.toString()
  }
}

object ScriptEngine {
  import java.util.Arrays.asList
  import scala.util.Properties.versionString

  class Factory extends ScriptEngineFactory {
    val driver = new ReplDriver(Array("-usejavacp"), Console.out, None);

    def getEngineName() = "Scala REPL"
    def getEngineVersion() = "3.0"
    def getExtensions() = asList("scala")
    def getLanguageName() = "Scala"
    def getLanguageVersion() = versionString
    def getMimeTypes() = asList("application/x-scala")
    def getNames() = asList("scala")

    def getMethodCallSyntax(obj: String, m: String, args: String*): String = null

    def getOutputStatement(toDisplay: String): String = null

    def getParameter(key: String): Object = key match {
      case JScriptEngine.ENGINE           => getEngineName
      case JScriptEngine.ENGINE_VERSION   => getEngineVersion
      case JScriptEngine.LANGUAGE         => getLanguageName
      case JScriptEngine.LANGUAGE_VERSION => getLanguageVersion
      case JScriptEngine.NAME             => getNames.get(0)
      case _ => null
    }

    def getProgram(statements: String*): String = null

    def getScriptEngine: JScriptEngine = {
      new ScriptEngine(this)
    }
  }
}

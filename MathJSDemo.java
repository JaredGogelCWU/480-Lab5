import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class MathJSDemo {
    protected static String MATHJS_URL =
            "http://cdnjs.cloudflare.com/ajax/libs/mathjs/1.0.1/math.js";

    protected ScriptEngine engine;

    public MathJSDemo() throws MalformedURLException, ScriptException, IOException {
        ScriptEngineManager manager = new ScriptEngineManager ();
        engine = manager.getEngineByName ("js");

        engine.eval(new InputStreamReader(new URL(MATHJS_URL).openStream()));
        engine.eval("var parser = math.parser();");
        engine.eval("var precision = 0;");
        engine.eval("var sin = Math.sin");
        engine.eval("var cos = Math.cos");
        engine.eval("var tan = Math.tan");
        engine.eval("var cot = 1.0/Math.tan");
        //engine.eval("var log = Math.log10()");
        engine.eval("var ln = Math.log");
    }

    public String eval (String expr) throws ScriptException {
        String script = "math.format(parser.eval('" + expr + "'), precision);";
        return (String) engine.eval(script);
    }
}

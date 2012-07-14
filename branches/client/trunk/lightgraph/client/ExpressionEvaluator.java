package lightgraph.client;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: melkor
 * Date: 6/3/12
 * Time: 1:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExpressionEvaluator {
    /**
     * Determines the column format.
     */
    final String col_reg_ex = "c[0-9]+";
    final Pattern column_pattern = Pattern.compile(col_reg_ex);

    Context context;
    Scriptable scope;

    boolean running = true;

    ExecutorService event_loop = Executors.newSingleThreadExecutor();

    ExpressionEvaluator(){
        event_loop.submit(new Runnable(){
            public void run(){

                context = Context.enter();
                scope = context.initStandardObjects();

                scope.put("count",scope,0);
                scope.put("sum",scope,0);


            }

        });

    }

    public boolean testExpression(String expression){
        final String exp = expression;
        final TestResult tr = new TestResult();

        Future<TestResult> future = event_loop.submit(
                new Runnable(){
                    public void run(){
                        tr.result = postTestExpression(exp);
                    }
            }, tr);


        try {
            TestResult completed = future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return tr.getResult();
    }
    private boolean postTestExpression(String expression){
        String[] columns = getColumns(expression);
        StringBuilder script = new StringBuilder();
        for(String c: columns){

            script.append("var ").append(c).append("=1; ");

        }
        script.append(expression).append(";");

        try{
            Object a = context.evaluateString(scope, script.toString(),"ValidateExpression",1,null);
            double d = Double.parseDouble(a.toString());

            return true;
        }catch(Exception e){
            //e.printStackTrace();
            return false;
        }

    }

    public String[] getColumns(String statement){

        Matcher m = column_pattern.matcher(statement);
        HashSet<String> columns = new HashSet<String>();
        while(m.find()){

            int s = m.start();
            int e = m.end();

            columns.add(statement.substring(s,e));


        }

        return columns.toArray(new String[columns.size()]);

    }

    public Function createJSFunction(String exp, final String name, String[] columns){
        final StringBuilder str_fun = new StringBuilder().append("function ").append(name);

        if(columns.length>0){
            String sep = "(";
            for(String s: columns){

                str_fun.append(sep);
                str_fun.append(s);
                sep = ",";

            }
            str_fun.append(")");
        }else{
            str_fun.append("()");
        }
        str_fun.append("{ return ").append(exp).append(";};");

        Future<Function> future = event_loop.submit(
                new Callable<Function>(){
                    public Function call(){
                        return context.compileFunction(scope,str_fun.toString(), name,1,null);
                    }
                }
        );

        try {
            return future.get();
        } catch (InterruptedException|ExecutionException e) {
            return null;
        }
    }

    /**
     * Calls a java script function which can be overridden
     * so that the user can initialize variables and such.
     *
     */
    public void startEvaluations(){

    }

    /**
     * Called when a data column is about to begin.
     *
     */
    public void startColumn(){

        scope.put("count",scope,0);
        scope.put("sum",scope,0);

    }

    /**
     * Calls a javascript function that can be overrode.
     */
    public void finishEvaluations(){

    }
    /**
     * A parsed and evaluatable function is passed with arguments.
     *
     * @param args the arguments of function
     * @param expression
     * @return
     */

    public double evaluate(Double[] args, LGExpression expression){
        Function f = expression.function;
        double b =(Double)f.call(context, scope, f, args);
        return b;
    }


    public void verify(String s){
        System.out.println(s);
        String[] columns = getColumns(s);
        for(String s1: columns){

            System.out.print(s1 + " , ") ;

        }
        if(testExpression(s)){
            System.out.println("evaluating: ");
            Function f = createJSFunction(s,"verify",columns);
            for(int i = 0; i<10; i++){
                Double[] args = new Double[columns.length];
                for(int j = 0; j<args.length; j++){
                    args[j] = i*0.1;
                }
                Double b =(Double)f.call(context, scope, f, args);
                System.out.println(b);
            }


        } else{
            System.out.println(" failed");
        }

    }


    public static void main(String[] args){
        ExpressionEvaluator exp_eval = new ExpressionEvaluator();

        exp_eval.verify("c1");
        exp_eval.verify("c1 + c2");
        exp_eval.verify("c1 * c2");
        exp_eval.verify("c1*c2");
        exp_eval.verify("c1*c2 + c3*c4 + c5");
        exp_eval.verify("1/(c1-c1)");
        exp_eval.verify("count++");
        exp_eval.verify("Math.log(c1)");

        System.out.println(1^0);
    }

}

class LGExpression{
    /* javascript function that will be called. */
    Function function;

    /* reverence to the column numbers that will be used. */
    int[] args;



}

class TestResult{
    boolean result;
    public boolean getResult(){
        return result;
    }
}

class FunctionResult{
    Function result;
}
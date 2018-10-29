package gw.lang;

import gw.fs.IDirectory;
import gw.lang.init.ClasspathToGosuPathEntryUtil;
import gw.lang.init.GosuInitialization;
import gw.lang.reflect.TypeSystem;
import gw.lang.reflect.gs.BytecodeOptions;
import gw.test.Suite;
import gw.test.TestEnvironment;
import junit.framework.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Typical JVM args:
 *   -Dgw.tests.skip.knownbreak=true
 *   -DcheckedArithmetic=true
 */
public class GosuScratchSuite extends Suite
{
  public static Test suite()
  {
    BytecodeOptions.enableAggressiveVerification();
    return new GosuScratchSuite()
            .withTestEnvironment( new ScratchTestEnvironment() )
      .withTest( "gw.specification.temp.DefaultMethodsTest" );
    //  .withTest( "gw.specContrib.rexpod.CoreIterableParallelizationTest" );
    //  .withTest( "gw.internal.gosu.parser.classTests.gwtest.dynamic.JsonTest" );
  }

  private static class ScratchTestEnvironment extends TestEnvironment
  {
    @Override
    public void initializeTypeSystem()
    {
      List<IDirectory> classpath = constructClasspathFromSystemClasspath();
      for( IDirectory file : new ArrayList<>( classpath ) )
      {
        if( file.getName().endsWith( "classes" ) )
        {
          classpath.add( file.getParent().dir( "test-classes" ) );
        }
      }
      GosuInitialization.instance( TypeSystem.getExecutionEnvironment() )
        .initializeRuntime( ClasspathToGosuPathEntryUtil.convertClasspathToGosuPathEntries( classpath ) );
    }
  }
}

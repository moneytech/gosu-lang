package gw.lang.init;

import gw.fs.IDirectory;
import gw.fs.IResource;
import gw.lang.Gosu;
import gw.lang.reflect.TypeSystem;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import manifold.internal.host.RuntimeManifoldHost;

public class GosuRuntimeManifoldHost extends RuntimeManifoldHost
{
  public static GosuRuntimeManifoldHost get()
  {
    return (GosuRuntimeManifoldHost)RuntimeManifoldHost.get();
  }

  public GosuRuntimeManifoldHost()
  {
    System.out.println( getClass().getTypeName() );
  }

  @Override
  public ClassLoader getActualClassLoader()
  {
    return TypeSystem.getGosuClassLoader().getActualLoader();
  }

  @Override
  public void init( List<File> sourcepath, List<File> classpath )
  {
    if( Gosu.bootstrapGosuWhenInitiatedViaClassfile() ) {
      // Assuming we are in runtime, we push the root module in the case where the process was started with java.exe and not gosu.cmd
      // In other words a Gosu class can be loaded directly from classfile in a bare bones Java program where only the Gosu runtime is
      // on the classpath and no module was pushed prior to loading.
      TypeSystem.pushModule( TypeSystem.getGlobalModule() );
    }

    if( classpath.isEmpty() )
    {
      classpath = new ArrayList<>();
    }
    classpath.addAll( removeNonFiles( TypeSystem.getGlobalModule().getJavaClassPath() ) );
    if( sourcepath.isEmpty() )
    {
      sourcepath = new ArrayList<>();
    }
    sourcepath.addAll( removeNonFiles( TypeSystem.getGlobalModule().getSourcePath() ) );

    if( !sourcepath.isEmpty() )
    {
      // paths are set explicitly, initialize directly from them
      initDirectly( sourcepath, classpath );
      return;
    }

    // paths are empty, init includes classpath from classloader
    super.init( sourcepath, classpath );
  }

  private List<File> removeNonFiles( List<IDirectory> dirs )
  {
    return dirs.stream()
      .filter( IResource::isJavaFile )
      .map( IResource::toJavaFile )
      .collect( Collectors.toList() );
  }
}

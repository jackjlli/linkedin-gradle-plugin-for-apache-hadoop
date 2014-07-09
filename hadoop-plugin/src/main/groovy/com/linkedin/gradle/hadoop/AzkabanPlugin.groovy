package com.linkedin.gradle.hadoop;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

class AzkabanPlugin implements Plugin<Project> {
  AzkabanExtension azkabanExtension;
  NamedScope globalScope = new NamedScope("");
  Project project;

  @Override
  public void apply(Project project) {
    this.azkabanExtension = new AzkabanExtension(project, globalScope);
    this.project = project;

    // Add the extensions that expose the DSL to users.
    project.extensions.add("azkaban", azkabanExtension);
    project.extensions.add("globalScope", globalScope);

    project.extensions.add("global", this.&global);
    project.extensions.add("lookup", this.&lookup);
    project.extensions.add("propertyFile", this.&propertyFile);
    project.extensions.add("workflow", this.&workflow);

    project.extensions.add("azkabanJob", this.&azkabanJob);
    project.extensions.add("commandJob", this.&commandJob);
    project.extensions.add("hiveJob", this.&hiveJob);
    project.extensions.add("javaJob", this.&javaJob);
    project.extensions.add("javaProcessJob", this.&javaProcessJob);
    project.extensions.add("kafkaPushJob", this.&kafkaPushJob);
    project.extensions.add("pigJob", this.&pigJob);
    project.extensions.add("voldemortBuildPushJob", this.&voldemortBuildPushJob);

    // Add the Gradle task that checks and evaluates the DSL. Plugin users
    // should have their build tasks depend on this task.
    project.tasks.create("buildAzkabanFlow") {
      description = "Builds Azkaban job files from the Azkaban DSL. Have your build task depend on this task.";
      group = "Hadoop Plugin";

      doLast {
        AzkabanDslChecker checker = new AzkabanDslChecker();
        if (!checker.checkAzkabanExtension(azkabanExtension)) {
          throw new Exception("AzkabanDslChecker FAILED");
        }

        logger.lifecycle("AzkabanDslChecker PASSED");
        azkabanExtension.build();
      }
    }
  }

  Object global(Object object) {
    if (globalScope.contains(object.name)) {
      throw new Exception("An object with name ${object.name} requested to be global is already bound in global scope");
    }
    globalScope.bind(object.name, object);
    return object;
  }

  Object lookup(String name) {
    return globalScope.lookup(name);
  }

  Object lookup(String name, Closure configure) {
    Object boundObject = lookup(name);
    if (boundObject == null) {
      return null;
    }
    project.configure(boundObject, configure);
    return boundObject;
  }

  AzkabanProperties propertyFile(String name, Closure configure) {
    AzkabanProperties props = new AzkabanProperties(name);
    globalScope.bind(name, props);
    project.configure(props, configure);
    return props;
  }

  AzkabanWorkflow workflow(String name, Closure configure) {
    AzkabanWorkflow flow = new AzkabanWorkflow(name, project, globalScope);
    globalScope.bind(name, flow);
    project.configure(flow, configure);
    return flow;
  }

  AzkabanJob addAndConfigure(AzkabanJob job, Closure configure) {
    globalScope.bind(job.name, job);
    project.configure(job, configure);
    return job;
  }

  AzkabanJob azkabanJob(String name, Closure configure) {
    return addAndConfigure(new AzkabanJob(name), configure);
  }

  CommandJob commandJob(String name, Closure configure) {
    return addAndConfigure(new CommandJob(name), configure);
  }

  HiveJob hiveJob(String name, Closure configure) {
    return addAndConfigure(new HiveJob(name), configure);
  }

  JavaJob javaJob(String name, Closure configure) {
    return addAndConfigure(new JavaJob(name), configure);
  }

  JavaProcessJob javaProcessJob(String name, Closure configure) {
    return addAndConfigure(new JavaProcessJob(name), configure);
  }

  KafkaPushJob kafkaPushJob(String name, Closure configure) {
    return addAndConfigure(new KafkaPushJob(name), configure);
  }

  PigJob pigJob(String name, Closure configure) {
    return addAndConfigure(new PigJob(name), configure);
  }

  VoldemortBuildPushJob voldemortBuildPushJob(String name, Closure configure) {
    return addAndConfigure(new VoldemortBuildPushJob(name), configure);
  }
}
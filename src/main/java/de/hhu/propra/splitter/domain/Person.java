package de.hhu.propra.splitter.domain;

public class Person {
  private String gitHubName;

  public String getGitHubName() {
    return gitHubName;
  }

  public Person(String gitHubName) {
    this.gitHubName = gitHubName;
  }
}

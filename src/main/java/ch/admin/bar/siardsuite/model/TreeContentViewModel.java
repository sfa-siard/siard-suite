package ch.admin.bar.siardsuite.model;


public class TreeContentViewModel {
  private String title;
  private Model model;


  public TreeContentViewModel(String title, Model model  ) {
    this.title = title;
    this.model =model;
  }

  public String getTitle() {
    return this.title;
  }

  public Model getModel() {
    return this.model;
  }
}

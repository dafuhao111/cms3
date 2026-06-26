package cn.edu.guet.cms3.entity;

public enum MenuType {
    DIRECTORY("目录"),
    MENU("菜单"),
    BUTTON("按钮");

    private String value;

    MenuType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

package utils.xmlObjects;

import javax.xml.bind.annotation.XmlAttribute;


public class Description {
    private String Uid;
    private String status;
    private String FullName;
    private String INN;

    @XmlAttribute(name="Uid")
    public String getUid(){
        return Uid;
    }

    public void setUid(String Uid) {
        this.Uid = Uid;
    }

    @XmlAttribute(name="Status")
    public String getStatus(){
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @XmlAttribute(name="FullName")
    public String getFullName(){
        return FullName;
    }

    public void setFullName(String FullName) {
        this.FullName = FullName;
    }

    @XmlAttribute(name="INN")
    public String getINN(){
        return INN;
    }

    public void setINN(String INN) {
        this.INN = INN;
    }

}


package utils.xmlObjects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;


@XmlRootElement(name="Message", namespace="http://smb.mil.ru/integration/control")
public class Message {
    private List<Reorganisation> reorganisationList;

    @XmlElement(name="Reorganisation")
    public List<Reorganisation> getReorganisationList(){
        return reorganisationList;
    }

    public void setReorganisationList(List<Reorganisation> reorganisationList) {
        this.reorganisationList = reorganisationList;
    }

}

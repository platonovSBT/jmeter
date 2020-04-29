package utils.xmlObjects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;


@XmlRootElement(name="root")
public class FakeRoot {
    private List<Reorganisation> reorganisationList;

    @XmlElement(name="Reorganisation")
    public List<Reorganisation> getReorganisationList(){
        return reorganisationList;
    }

    public void setReorganisationList(List<Reorganisation> reorganisationList) {
        this.reorganisationList = reorganisationList;
    }
}

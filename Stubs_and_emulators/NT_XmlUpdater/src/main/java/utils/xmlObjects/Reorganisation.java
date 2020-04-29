package utils.xmlObjects;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

/**
 * Created by horunzhev on 31.01.19.
 */
public class Reorganisation {

    private List<String> reorganisationFormList;
    private List<Description> descriptionList;
    private String correctedOpUID;
    private String correctionDate;
    private String reorganisationDate;
    private String opUID;

    @XmlElement(name="ReorganisationForm")
    public List<String> getReorganisationForm(){
        return reorganisationFormList;
    }

    public void setReorganisationForm(List<String> reorganisationFormList) {
        this.reorganisationFormList = reorganisationFormList;
    }

    @XmlElement(name="Description")
    public List<Description> getDescriptionList(){
        return descriptionList;
    }

    public void setDescriptionList(List<Description> descriptionList) {
        this.descriptionList = descriptionList;
    }

    @XmlAttribute(name="ReorganisationDate")
    public String getReorganisationDate(){
        return reorganisationDate;
    }

    public void setReorganisationDate(String reorganisationDate) {
        this.reorganisationDate = reorganisationDate;
    }

    @XmlAttribute(name="CorrectedOpUID")
    public String getCorrectedOpUID(){
        return correctedOpUID;
    }

    public void setCorrectedOpUID(String correctedOpUID) {
        this.correctedOpUID = correctedOpUID;
    }

    @XmlAttribute(name="CorrectionDate")
    public String getCorrectionDate(){
        return correctionDate;
    }

    public void setCorrectionDate(String correctionDate) {
        this.correctionDate = correctionDate;
    }

    @XmlAttribute(name="OpUID")
    public String getOpUID(){
        return opUID;
    }

    public void setOpUID(String opUID) {
        this.opUID = opUID;
    }

}

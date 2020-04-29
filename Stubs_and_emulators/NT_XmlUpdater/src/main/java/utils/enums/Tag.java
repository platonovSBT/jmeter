package utils.enums;

public enum Tag {
    Accs("Accs"),
    DocType("DocType"),
    AccCreateOps("AccCreateOps"),
    AccCloseOps("AccCloseOps"),
    AccStats("AccStats"),
    Acc("Acc"),
    ContractorCorrAccNum("ContractorCorrAccNum"),
    AccPrev("AccPrev"),
    AccReps("AccReps"),
    AccRep("AccRep"),
    AccFrom("AccFrom"),
    AccTo("AccTo"),
    Addr("Addr"),
    CreditOps("CreditOps"),
    DebitOps("DebitOps"),
    RightsTransfer("RightsTransfer"),
    DebitOpsToSpecialAcc("DebitOpsToSpecialAcc"),
    Contractors("Contractors"),
    Contractor("Contractor"),
    ContractorCorr("ContractorCorr"),
    ContractorCorrAcc("ContractorCorrAcc"),
    Owner("Owner"),
    Contracts("Contracts"),
    Contract("Contract"),
    RightsSource("RightsSource"),
    RightsTarget("RightsTarget"),
    Bank("Bank"),
    MetaInf("MetaInf"),
    AccEditOps("AccEditOps"),
    FileLinks("FileLinks"),
    FileAttach("FileAttach"),
    GOZUID("GOZUID"),
    Assignee("Assignee"),
    Reorganisation("Reorganisation"),
    ReorganisationForm("ReorganisationForm"),
    Description("Description"),
    NoData("NoData"),
    Nodata("Nodata"),
    Deposit("Deposit"),
    Loan("loan"),
    License("License"),
    OrigOpUID("OrigOpUID"),
    Containers("Containers");


    private final String text;

    Tag(final String text) {
        this.text = text;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return text;
    }
}

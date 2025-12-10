public class AssociationBidirectionnelle {
    private Point point;
    private Disque disque;

    public AssociationBidirectionnelle(Point point, Disque disque) {
        this.point = point;
        this.disque = disque;
    }

    public void setDisque(Disque disque) {
        this.disque = disque;
    }

    public void setPoint(Point point) {
        this.point = point;
    }
}

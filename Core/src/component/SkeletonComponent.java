package component;

import animation.Skeleton;

/**
 * Created by germangb on 30/06/16.
 */
public class SkeletonComponent extends Component {

    /** Joint structure */
    private Skeleton skeleton;

    public SkeletonComponent (Skeleton skeleton) {
        this.skeleton = skeleton;
    }

    /**
     * Skeleton getter
     * @return
     */
    public Skeleton getSkeleton() {
        return skeleton;
    }

    /**
     * Skeleton setter
     * @param skeleton
     */
    public void setSkeleton(Skeleton skeleton) {
        this.skeleton = skeleton;
    }
}

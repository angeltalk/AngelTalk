package act.sds.samsung.angelman.domain.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryItemModel {
    public int type;
    public int status;
}

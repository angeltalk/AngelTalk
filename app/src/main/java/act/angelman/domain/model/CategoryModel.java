package act.angelman.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryModel{
    public String _id;
    public String title;
    public int index;
    public int icon;
    public int color;
}

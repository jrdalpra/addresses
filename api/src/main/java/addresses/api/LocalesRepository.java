package addresses.api;

import java.util.List;

import com.zeroturnaround.rebellabs.addresses.model.Country;
import com.zeroturnaround.rebellabs.addresses.model.Locale;

public interface LocalesRepository extends
		CommonsMethodsForARepository<Locale, Long> {

	List<Locale> listWhereCountryEquals(Country localized, int page, int max);

}

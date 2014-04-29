package com.zeroturnaround.rebellabs.addresses.vraptor.infra;

import javax.inject.Inject;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.interceptor.ApplicationLogicException;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.resource.ResourceMethod;

import com.zeroturnaround.rebellabs.addresses.api.exceptions.NotFoundException;

@Intercepts
public class NotFoundInterceptor implements Interceptor {

    @Inject
    private Result result;

    @Override
    public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance) throws InterceptionException {
        try {
            stack.next(method, resourceInstance);
        } catch (ApplicationLogicException error) {
            error.printStackTrace();
            if (error.getCause() instanceof NotFoundException) {
                result.notFound();
                return;
            }
            throw error;
        }
    }

    @Override
    public boolean accepts(ResourceMethod method) {
        return true;
    }

}

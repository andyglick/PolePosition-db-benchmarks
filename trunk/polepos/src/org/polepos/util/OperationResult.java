/*
This file is part of the PolePosition database benchmark
http://www.polepos.org

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public
License along with this program; if not, write to the Free
Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
MA  02111-1307, USA. */
package org.polepos.util;

/**
 * @author roman.stoffel@gamlor.info
 * @since 21.06.11
 */
public final class OperationResult<T> extends OperationSuccess {
    private final T resultData;

    private OperationResult(Exception exeception) {
        super(exeception);
        this.resultData = null;
    }
    private OperationResult(T resultData) {
        super(null);
        if(null==resultData){
            throw new IllegalArgumentException("Null is a invalid result");
        }
        this.resultData = resultData;
    }

    public T getResultData() {
        if(wasSuccessful()){
            return resultData;
        }
        throw JavaLangUtils.rethrow(getException());
    }

    public static <T> OperationResult<T> fail(Exception exception){
        return new OperationResult<T>(exception);
    }
    public static <T> OperationResult<T> success(T result){
        return new OperationResult<T>(result);
    }

    public <TOther> OperationResult<TOther> failToAny() {
        if(!wasSuccessful()){
            return OperationResult.fail(getException());
        }
        throw new IllegalStateException("Cannot cast a success to other type");
    }

}


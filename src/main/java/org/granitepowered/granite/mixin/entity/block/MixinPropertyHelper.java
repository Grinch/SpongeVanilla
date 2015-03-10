/*
 * License (MIT)
 *
 * Copyright (c) 2014-2015 Granite Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.granitepowered.granite.mixin.entity.block;

import com.google.common.base.Optional;
import mc.IProperty;
import mc.PropertyHelper;
import org.spongepowered.api.block.BlockProperty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Collection;

@Mixin(value = PropertyHelper.class, remap = false)
public abstract class MixinPropertyHelper implements BlockProperty, IProperty {
    @Shadow
    private String name;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Collection getValidValues() {
        return getAllowedValues();
    }

    @Override
    public String getNameForValue(Comparable comparable) {
        return getName(comparable);
    }

    @Override
    public Optional getValueForName(String s) {
        for (Object comparable : getValidValues()) {
            if (getNameForValue((Comparable) comparable).equals(s)) {
                return Optional.of(comparable);
            }
        }
        return Optional.absent();
    }
}
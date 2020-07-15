package cn.nukkit.blockproperty;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.math.NukkitMath;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashSet;
import java.util.Set;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
@ParametersAreNonnullByDefault
public final class ArrayBlockProperty<E> extends BlockProperty<E> {
    @Nonnull
    private final E[] universe;
    
    private final int defaultMeta;
    
    private final Class<E> eClass;
    
    private final boolean ordinal;
    
    private static <E> E[] checkUniverseLength(E[] universe) {
        Preconditions.checkArgument(universe.length > 0, "The universe can't be empty");
        return universe;
    }


    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public ArrayBlockProperty(String name, boolean exportedToItem, E[] universe, E defaultValue, int bitSize, String persistenceName) {
        this(name, exportedToItem, universe, defaultValue, bitSize, persistenceName, false);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public ArrayBlockProperty(String name, boolean exportedToItem, E[] universe, E defaultValue, int bitSize, String persistenceName, boolean ordinal) {
        super(name, exportedToItem, bitSize, persistenceName);
        this.ordinal = ordinal;
        this.universe = universe.clone();
        //noinspection unchecked
        this.eClass = (Class<E>) universe.getClass().getComponentType();
        checkUniverseLength(universe);
        Set<E> elements = new HashSet<>();
        int defaultMetaIndex = -1;
        for (int i = 0; i < this.universe.length; i++) {
            E element = this.universe[i];
            Preconditions.checkNotNull(element, "The universe can not contain null values");
            Preconditions.checkArgument(elements.add(element), "The universe can not have duplicated elements");
            if (element.equals(defaultValue)) {
                defaultMetaIndex = i;
            }
        }
        
        Preconditions.checkArgument(defaultMetaIndex >= 0, "The universe must contain the default value instance");
        this.defaultMeta = defaultMetaIndex;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public ArrayBlockProperty(String name, boolean exportedToItem, E[] universe, E defaultValue, int bitSize) {
        this(name, exportedToItem, universe, defaultValue, bitSize, name);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public ArrayBlockProperty(String name, boolean exportedToItem, E[] universe, E defaultValue) {
        this(name, exportedToItem, universe, defaultValue, NukkitMath.bitLength(universe.length - 1));
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public ArrayBlockProperty(String name, boolean exportedToItem, E[] universe) {
        this(name, exportedToItem, checkUniverseLength(universe), universe[0]);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public ArrayBlockProperty(String name, boolean exportedToItem, Class<E> enumClass) {
        this(name, exportedToItem, enumClass.getEnumConstants());
    }
    
    public ArrayBlockProperty<E> ordinal(boolean ordinal) {
        if (ordinal == this.ordinal) {
            return this;
        }
        return new ArrayBlockProperty<>(getName(), isExportedToItem(), universe, getValueForMeta(defaultMeta), getBitSize(), getPersistenceName(), ordinal);
    }

    @Override
    public int getMetaForValue(@Nullable E value) {
        if (value == null) {
            return defaultMeta;
        }
        for (int i = 0; i < universe.length; i++) {
            if (universe[i].equals(value)) {
                return i;
            }
        }
        throw new IllegalArgumentException(value+" is not valid for this property");
    }

    @Nonnull
    @Override
    public E getValueForMeta(int meta) {
        return universe[meta];
    }

    @Override
    public int getIntValueForMeta(int meta) {
        return meta;
    }
    
    @Nonnull
    @Override
    public String getPersistenceValueForMeta(int meta) {
        if (isOrdinal()) {
            return Integer.toString(meta);
        }
        return getValueForMeta(meta).toString().toLowerCase();
    }

    @Override
    protected void validate(@Nullable E value) {
        for (E object : universe) {
            if (object == value) {
                return;
            }
        }
        throw new IllegalArgumentException(value+" is not valid for this property");
    }

    @Override
    protected void validateMeta(int meta) {
        Preconditions.checkElementIndex(meta, universe.length);
    }

    @Override
    public Class<E> getValueClass() {
        return eClass;
    }

    public boolean isOrdinal() {
        return ordinal;
    }
}

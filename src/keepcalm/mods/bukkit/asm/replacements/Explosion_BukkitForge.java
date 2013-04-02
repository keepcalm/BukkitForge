package keepcalm.mods.bukkit.asm.replacements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import keepcalm.mods.events.EventFactory;

import com.eoware.asm.asmagic.AsmagicReplaceMethod;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class Explosion_BukkitForge
{
    public boolean a = false; //isFlaming
    public boolean b = true; //isSmoking
    private int i = 16; //field_77289_h
    private Random j = new Random(); //j
    public double c; //explosionX
    public double d; //explosionY
    public double e; //e
    public Entity f; //exploder
    public float g; //explosionSize
    private World k; //worldObj

    /** A list of ChunkPositions of blocks affected by this explosion */
    public List h = new ArrayList(); //h
    private Map l = new HashMap(); //field_77288_k
    
    @AsmagicReplaceMethod
    public void a(boolean par1)
    {
        //BukkitForge start - Patch the class instance to being able to compile
        Explosion newThis = null;
        try {
            newThis = (Explosion)Class.forName(this.getClass().getName()).cast(this);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        //BukkitForge end
        
        this.k.playSoundEffect(this.c, this.d, this.e, "random.explode", 4.0F, (1.0F + (this.k.rand.nextFloat() - this.k.rand.nextFloat()) * 0.2F) * 0.7F);

        if (this.g >= 2.0F && this.b)
        {
            this.k.spawnParticle("hugeexplosion", this.c, this.d, this.e, 1.0D, 0.0D, 0.0D);
        }
        else
        {
            this.k.spawnParticle("largeexplode", this.c, this.d, this.e, 1.0D, 0.0D, 0.0D);
        }

        Iterator iterator;
        ChunkPosition chunkposition;
        int i;
        int j;
        int k;
        int l;

        if (this.b)
        {
            //BukkitForge start
            if(EventFactory.onEntityExplode(this.h, this.k, this.f, this.c, this.d, this.e, this.g))
            {
                this.g = -1F;
                return;
            }
            //BukkitForge end
            iterator = this.h.iterator();

            while (iterator.hasNext())
            {
                chunkposition = (ChunkPosition)iterator.next();
                i = chunkposition.x;
                j = chunkposition.y;
                k = chunkposition.z;
                l = this.k.getBlockId(i, j, k);

                if (par1)
                {
                    double d0 = (double)((float)i + this.k.rand.nextFloat());
                    double d1 = (double)((float)j + this.k.rand.nextFloat());
                    double d2 = (double)((float)k + this.k.rand.nextFloat());
                    double d3 = d0 - this.c;
                    double d4 = d1 - this.d;
                    double d5 = d2 - this.e;
                    double d6 = (double)MathHelper.sqrt_double(d3 * d3 + d4 * d4 + d5 * d5);
                    d3 /= d6;
                    d4 /= d6;
                    d5 /= d6;
                    double d7 = 0.5D / (d6 / (double)this.g + 0.1D);
                    d7 *= (double)(this.k.rand.nextFloat() * this.k.rand.nextFloat() + 0.3F);
                    d3 *= d7;
                    d4 *= d7;
                    d5 *= d7;
                    this.k.spawnParticle("explode", (d0 + this.c * 1.0D) / 2.0D, (d1 + this.d * 1.0D) / 2.0D, (d2 + this.e * 1.0D) / 2.0D, d3, d4, d5);
                    this.k.spawnParticle("smoke", d0, d1, d2, d3, d4, d5);
                }

                if (l > 0)
                {
                    Block block = Block.blocksList[l];

                    if (block.canDropFromExplosion(newThis)) //BukkitForge - this -> newThis
                    {
                        block.dropBlockAsItemWithChance(this.k, i, j, k, this.k.getBlockMetadata(i, j, k), 1.0F / this.g, 0);
                    }

                    this.k.setBlock(i, j, k, 0, 0, 3);
                    block.onBlockDestroyedByExplosion(this.k, i, j, k, newThis); //BukkitForge - this -> newThis
                }
            }
        }

        if (this.a)
        {
            iterator = this.h.iterator();

            while (iterator.hasNext())
            {
                chunkposition = (ChunkPosition)iterator.next();
                i = chunkposition.x;
                j = chunkposition.y;
                k = chunkposition.z;
                l = this.k.getBlockId(i, j, k);
                int i1 = this.k.getBlockId(i, j - 1, k);

                if (l == 0 && Block.opaqueCubeLookup[i1] && this.j.nextInt(3) == 0)
                {
                    this.k.setBlock(i, j, k, Block.fire.blockID);
                }
            }
        }
    }
}

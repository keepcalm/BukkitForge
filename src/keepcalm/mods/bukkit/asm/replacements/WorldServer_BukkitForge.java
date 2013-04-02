package keepcalm.mods.bukkit.asm.replacements;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.eoware.asm.asmagic.AsmagicReplaceMethod;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.packet.Packet60Explosion;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.WorldServer;

public class WorldServer_BukkitForge 
{
    public List playerEntities = new ArrayList();
    
    //newExplosion
    @AsmagicReplaceMethod
    public Explosion a(Entity par1Entity, double par2, double par4, double par6, float par8, boolean par9, boolean par10)
    {
        //BukkitForge start - Patch the class instance to being able to compile
        WorldServer newThis = null;
        try {
            newThis = (WorldServer)Class.forName(this.getClass().getName()).cast(this);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        //BukkitForge end
        Explosion explosion = new Explosion(newThis, par1Entity, par2, par4, par6, par8); //BukkitForge - use newThis instead of this
        explosion.isFlaming = par9;
        explosion.isSmoking = par10;
        explosion.doExplosionA();
        explosion.doExplosionB(false);

        if (!par10)
        {
            explosion.affectedBlockPositions.clear();
        }
        //BukkitForge start - if size is negative cancel
        if (explosion.explosionSize < 0)
            return explosion;
        //BukkitForge end

        Iterator iterator = this.playerEntities.iterator();

        while (iterator.hasNext())
        {
            EntityPlayer entityplayer = (EntityPlayer)iterator.next();

            if (entityplayer.getDistanceSq(par2, par4, par6) < 4096.0D)
            {
                ((EntityPlayerMP)entityplayer).playerNetServerHandler.sendPacketToPlayer(new Packet60Explosion(par2, par4, par6, par8, explosion.affectedBlockPositions, (Vec3)explosion.func_77277_b().get(entityplayer)));
            }
        }

        return explosion;
    }
}

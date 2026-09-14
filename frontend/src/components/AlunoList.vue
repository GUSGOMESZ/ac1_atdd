<template>
  <div class="card">
    <h2>Alunos Cadastrados ({{ alunos.length }})</h2>

    <p v-if="alunos.length === 0" style="color: #999; font-size: 0.9rem;">Nenhum aluno cadastrado.</p>

    <div v-for="aluno in alunos" :key="aluno.id" class="aluno-item">
      <div class="aluno-info">
        <strong>{{ aluno.nome }}</strong>
        <span class="email">{{ aluno.email }}</span>
        <span class="score">
          Uteis: {{ uteis(aluno) }} | Nao uteis: {{ naoUteis(aluno) }} | Score: {{ score(aluno) }}
        </span>
      </div>

      <div class="comentario-form">
        <input
          v-model="textos[aluno.id]"
          placeholder="Texto do comentário..."
          @keyup.enter="adicionarComentario(aluno.id, true)"
        />
        <button class="btn btn-success btn-sm" @click="adicionarComentario(aluno.id, true)">+ Útil</button>
        <button class="btn btn-sm" style="background:#e53935;color:white" @click="adicionarComentario(aluno.id, false)">+ Não Útil</button>
      </div>

      <div v-if="msgs[aluno.id]" class="alert alert-success" style="margin-top:6px; font-size:0.8rem">
        {{ msgs[aluno.id] }}
      </div>

      <div v-if="aluno.comentarios?.length" class="comentarios-lista">
        <div
          v-for="c in aluno.comentarios"
          :key="c.id"
          :class="['comentario-tag', c.util ? 'util' : 'nao-util']"
        >
          {{ c.util ? '✓' : '✗' }} {{ c.texto }}
        </div>
      </div>
      <p v-else style="font-size:0.8rem; color:#aaa; margin-top:8px">Nenhum comentário ainda.</p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({ alunos: Array })
const emit = defineEmits(['atualizar'])

const textos = ref({})
const msgs = ref({})

function uteis(aluno) {
  return aluno.comentarios?.filter(c => c.util).length ?? 0
}

function naoUteis(aluno) {
  return aluno.comentarios?.filter(c => !c.util).length ?? 0
}

function score(aluno) {
  return uteis(aluno) - naoUteis(aluno)
}

async function adicionarComentario(alunoId, util) {
  const texto = textos.value[alunoId]?.trim()
  if (!texto) return

  try {
    const res = await fetch(`/api/forum-rewards/alunos/${alunoId}/comentarios?util=${util}&texto=${encodeURIComponent(texto)}`, {
      method: 'POST'
    })
    if (!res.ok) throw new Error()
    textos.value[alunoId] = ''
    msgs.value[alunoId] = `Comentário ${util ? 'útil' : 'não útil'} adicionado!`
    setTimeout(() => { msgs.value[alunoId] = '' }, 2000)
    emit('atualizar')
  } catch {
    msgs.value[alunoId] = 'Erro ao adicionar comentário.'
  }
}
</script>

<style scoped>
.aluno-item {
  border: 1px solid #e0e0e0;
  border-radius: 6px;
  padding: 12px;
  margin-bottom: 12px;
}

.aluno-info {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  align-items: center;
  margin-bottom: 10px;
}

.email {
  color: #777;
  font-size: 0.85rem;
}

.score {
  font-size: 0.8rem;
  background: #e3f2fd;
  color: #1565c0;
  padding: 2px 8px;
  border-radius: 12px;
}

.comentario-form {
  display: flex;
  gap: 8px;
  align-items: center;
}

.comentario-form input {
  flex: 1;
}

.comentarios-lista {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 10px;
}

.comentario-tag {
  font-size: 0.78rem;
  padding: 3px 10px;
  border-radius: 12px;
}

.comentario-tag.util {
  background: #e8f5e9;
  color: #2e7d32;
  border: 1px solid #a5d6a7;
}

.comentario-tag.nao-util {
  background: #ffebee;
  color: #c62828;
  border: 1px solid #ef9a9a;
}
</style>
